package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.dto.UserManagementDtos;
import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.auth.service.otp.OtpPurpose;
import com.zarnab.panel.auth.service.otp.OtpService;
import com.zarnab.panel.auth.service.sms.SmsService;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.common.search.SpecificationBuilder;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.core.util.RoleUtil;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.req.InitiateTransferRequest;
import com.zarnab.panel.ingot.dto.req.VerifyTransferRequest;
import com.zarnab.panel.ingot.dto.res.InitiateTransferResponse;
import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.model.ReportIssueStatus;
import com.zarnab.panel.ingot.model.Transfer;
import com.zarnab.panel.ingot.model.TransferStatus;
import com.zarnab.panel.ingot.repository.IngotRepository;
import com.zarnab.panel.ingot.repository.ReportIssueRepository;
import com.zarnab.panel.ingot.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.zarnab.panel.common.translate.Translator.translate;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final IngotRepository ingotRepository;
    private final UserRepository userRepository;
    private final TransferRepository transferRepository;
    private final ReportIssueRepository reportIssueRepository;
    private final OtpService otpService;
    private final SmsService smsService;

    @Override
    @Transactional
    public InitiateTransferResponse initiateTransfer(InitiateTransferRequest request, String username) {
        User seller = userRepository.findByMobileNumber(username)
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));

        boolean sellerIsAdmin = RoleUtil.hasRole(seller, Role.ADMIN);
        boolean sellerIsCounter = RoleUtil.hasRole(seller, Role.COUNTER);
        boolean sellerIsCustomer = !sellerIsAdmin && !sellerIsCounter;

        User buyer = null;
        InitiateTransferRequest.TransferTarget to = request.getTo();

        if (sellerIsAdmin) {
            if (to != InitiateTransferRequest.TransferTarget.COUNTER) {
                throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER, "Admin can only transfer to Counter.");
            }
        } else if (sellerIsCustomer) {
            if (to != InitiateTransferRequest.TransferTarget.CUSTOMER && to != InitiateTransferRequest.TransferTarget.COUNTER) {
                throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER, "Customer can only transfer to Customer or Counter.");
            }
        } else {
            if (to != InitiateTransferRequest.TransferTarget.CUSTOMER && to != InitiateTransferRequest.TransferTarget.ZARNAB) {
                throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER, "Counter can only transfer to CUSTOMER or Zarnab.");
            }
        }

        if (to == InitiateTransferRequest.TransferTarget.CUSTOMER || to == InitiateTransferRequest.TransferTarget.COUNTER) {
            if (request.getBuyerMobileNumber() == null) {
                throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER, "Buyer mobile number is required.");
            }
            buyer = userRepository.findByMobileNumber(request.getBuyerMobileNumber())
                    .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));

            if (to == InitiateTransferRequest.TransferTarget.CUSTOMER && !(RoleUtil.hasRole(buyer, Role.CUSTOMER))) {
                throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER, "Buyer must be a Customer.");
            }
            if (to == InitiateTransferRequest.TransferTarget.COUNTER && !(RoleUtil.hasRole(buyer, Role.COUNTER))) {
                throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER, "Buyer must be a Counter.");
            }
        }

        List<Ingot> ingots = ingotRepository.findAllById(request.getIngotIds());
        if (ingots.size() != request.getIngotIds().size()) {
            throw new ZarnabException(ExceptionType.INGOT_NOT_FOUND);
        }

        if ((sellerIsCustomer || (sellerIsCounter && to != InitiateTransferRequest.TransferTarget.ZARNAB)) && ingots.size() > 1) {
            throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER, "Cannot transfer more than one ingot at a time.");
        }

        for (Ingot ingot : ingots) {
            if (!sellerIsAdmin && !ingot.getOwner().getId().equals(seller.getId())) {
                throw new ZarnabException(ExceptionType.INGOT_OWNERSHIP_ERROR);
            }
            if (buyer != null && ingot.getOwner() != null && buyer.getId().equals(ingot.getOwner().getId())) {
                String name = buyer.getNaturalPersonProfile().getFirstName() + " " + buyer.getNaturalPersonProfile().getLastName();
                throw new ZarnabException(ExceptionType.INGOT_ALREADY_OWNERSHIP, name);
            }
            if (reportIssueRepository.existsByIngotAndStatusIn(ingot, List.of(ReportIssueStatus.PENDING, ReportIssueStatus.APPROVED))) {
                throw new ZarnabException(ExceptionType.INGOT_IS_STOLEN);
            }
        }

        otpService.sendOtp(OtpPurpose.INGOT_TRANSFER, seller.getMobileNumber());

        String batchId = UUID.randomUUID().toString();
        for (Ingot ingot : ingots) {
            Transfer transfer = Transfer.builder()
                    .ingot(ingot)
                    .seller(seller)
                    .buyerMobileNumber(request.getBuyerMobileNumber())
                    .status(TransferStatus.PENDING_SELLER_VERIFICATION)
                    .batchId(batchId)
                    .build();
            transferRepository.save(transfer);
        }

        return new InitiateTransferResponse(batchId, UserManagementDtos.UserResponse.from(buyer));
    }


    @Override
    @Transactional
    public void verifyTransfer(VerifyTransferRequest request, String username) {
        User seller = userRepository.findByMobileNumber(username)
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));
        List<Transfer> transfers = transferRepository.findByBatchId(request.getBatchId());
        if (transfers.isEmpty()) {
            throw new ZarnabException(ExceptionType.TRANSFER_NOT_FOUND);
        }

        otpService.verifyOtp(OtpPurpose.INGOT_TRANSFER, seller.getMobileNumber(), request.getVerificationCode());

        User buyer = null;
        if (transfers.get(0).getBuyerMobileNumber() != null && !transfers.get(0).getBuyerMobileNumber().isEmpty()) {
            buyer = userRepository.findByMobileNumber(transfers.get(0).getBuyerMobileNumber())
                    .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));
        }

        for (Transfer transfer : transfers) {
            if (transfer.getStatus() != TransferStatus.PENDING_SELLER_VERIFICATION) {
                throw new ZarnabException(ExceptionType.TRANSFER_INVALID_STATUS);
            }
            if (!transfer.getSeller().getId().equals(seller.getId())) {
                throw new ZarnabException(ExceptionType.TRANSFER_SELLER_MISMATCH);
            }

            transfer.setBuyer(buyer);
            transfer.setStatus(TransferStatus.COMPLETED);

            Ingot ingot = transfer.getIngot();
            ingot.setOwner(buyer);
            ingotRepository.save(ingot);

            if (transfer.getBuyerMobileNumber() != null) {
                smsService.sendSms(transfer.getBuyerMobileNumber(), translate("transfer.success", transfer.getBuyerMobileNumber()));
            }
            transferRepository.save(transfer);
        }

    }

    @Override
    @Transactional
    public void cancelTransfer(Long transferId, String username) {
        User currentUser = userRepository.findByMobileNumber(username)
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new ZarnabException(ExceptionType.TRANSFER_NOT_FOUND));

        boolean isSeller = transfer.getSeller().equals(currentUser);

        if (!isSeller) {
            throw new ZarnabException(ExceptionType.TRANSFER_PERMISSION_DENIED);
        }

        if (transfer.getStatus() != TransferStatus.PENDING_SELLER_VERIFICATION) {
            throw new ZarnabException(ExceptionType.TRANSFER_INVALID_STATUS);
        }

        transfer.setStatus(TransferStatus.CANCELED);
        transferRepository.save(transfer);
    }

    @Override
    @Transactional(readOnly = true)
    public PageableResponse<IngotDtos.TransferDto> getTransfers(User user, PageableRequest pageableRequest) {
        pageableRequest.addToAliases("serial", "ingot.serial");

        Specification<Transfer> spec = SpecificationBuilder.buildSpecification(pageableRequest);

        boolean isAdminOrCounter = RoleUtil.hasRole(user, Role.ADMIN, Role.COUNTER);
        if (!isAdminOrCounter) {
            Specification<Transfer> userSecuritySpec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("seller"), user),
                            criteriaBuilder.equal(root.get("buyer"), user)
                    );
            spec = (spec == null) ? userSecuritySpec : spec.and(userSecuritySpec);
        }

        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), pageableRequest.getSort());

        Page<Transfer> transferPage = transferRepository.findAll(spec, pageable);

        List<IngotDtos.TransferDto> transferDtos = transferPage.getContent().stream()
                .map(IngotDtos.TransferDto::from)
                .collect(Collectors.toList());

        return new PageableResponse<>(
                transferDtos,
                transferPage.getTotalElements(),
                transferPage.getNumber(),
                transferPage.getSize()
        );
    }
}
