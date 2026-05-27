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
import com.zarnab.panel.core.entity.BaseEntity;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.core.util.RoleUtil;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.MonthlyWeightDashboard;
import com.zarnab.panel.ingot.dto.req.*;
import com.zarnab.panel.ingot.dto.res.InitiateQuickTransferResponse;
import com.zarnab.panel.ingot.dto.res.InitiateTransferResponse;
import com.zarnab.panel.ingot.dto.res.VerifyTransferResponse;
import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.model.ReportIssueStatus;
import com.zarnab.panel.ingot.model.Transfer;
import com.zarnab.panel.ingot.model.TransferStatus;
import com.zarnab.panel.ingot.repository.IngotRepository;
import com.zarnab.panel.ingot.repository.ReportIssueRepository;
import com.zarnab.panel.ingot.repository.TransferRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
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

    @Value("${zarnab.gold-price}")
    private long goldPrice;

    @Override
    @Transactional
    public InitiateTransferResponse initiateTransfer(InitiateTransferRequest request, User seller) {
        List<Ingot> ingots = ingotRepository.findAllById(request.getIngotIds());
        if (ingots.isEmpty() || ingots.size() != request.getIngotIds().size()) {
            throw new ZarnabException(ExceptionType.INGOT_NOT_FOUND);
        }
        List<Transfer> transfers = transferRepository.findBySellerAndIngotAndStatusIn(seller.getId(),
                ingots.stream().map(BaseEntity::getId).collect(Collectors.toSet()),
                List.of(TransferStatus.PENDING_RECEIVER_VERIFICATION, TransferStatus.PENDING_SENDER_VERIFICATION)
        );

        if (!transfers.isEmpty()) {
            throw new ZarnabException(ExceptionType.DUPLICATE_TRANSFER_REQUEST);
        }
        validateTransfer(seller, request.getBuyerMobileNumber(), ingots, request.getTo());

        otpService.sendOtp(OtpPurpose.INGOT_TRANSFER, seller.getMobileNumber());

        String batchId = UUID.randomUUID().toString();
        for (Ingot ingot : ingots) {
            Transfer transfer = Transfer.builder()
                    .ingot(ingot)
                    .seller(seller)
                    .buyerMobileNumber(request.getBuyerMobileNumber())
                    .status(TransferStatus.PENDING_SENDER_VERIFICATION)
                    .batchId(batchId)
                    .build();
            transferRepository.save(transfer);
        }

        User buyer = userRepository.findByMobileNumber(request.getBuyerMobileNumber()).orElse(null);
        return new InitiateTransferResponse(batchId, UserManagementDtos.UserResponse.from(buyer));
    }

    @Override
    @Transactional
    public InitiateQuickTransferResponse initiateQuickTransfer(InitiateQuickTransferRequest request) {
        Ingot ingot = ingotRepository.findBySerial(request.getIngotSerialNumber())
                .orElseThrow(() -> new ZarnabException(ExceptionType.INGOT_NOT_FOUND));

        if (ingot.getOwner() == null || !ingot.getOwner().getMobileNumber().equals(request.getSenderMobileNumber())) {
            throw new ZarnabException(ExceptionType.INGOT_OWNERSHIP_ERROR);
        }
        if (reportIssueRepository.existsByIngotAndStatusIn(ingot, List.of(ReportIssueStatus.PENDING, ReportIssueStatus.APPROVED))) {
            throw new ZarnabException(ExceptionType.INGOT_IS_STOLEN);
        }

        Optional<Transfer> transferOptional = transferRepository.findBySellerAndIngotAndStatus(
                ingot.getOwner().getId(),
                ingot.getId(),
                TransferStatus.PENDING_RECEIVER_VERIFICATION);

        if (transferOptional.isPresent()) {
            throw new ZarnabException(ExceptionType.DUPLICATE_TRANSFER_REQUEST);
        }

        otpService.sendOtp(OtpPurpose.INGOT_TRANSFER, request.getSenderMobileNumber());

        String batchId = UUID.randomUUID().toString();
        Transfer transfer = Transfer.builder()
                .ingot(ingot)
                .seller(ingot.getOwner())
                .status(TransferStatus.PENDING_SENDER_VERIFICATION)
                .batchId(batchId)
//                .buyerMobileNumber()
                .build();
        transferRepository.save(transfer);

        return new InitiateQuickTransferResponse(batchId);
    }

    @Transactional
    @Override
    public void verifySenderQuickTransfer(VerifyQuickTransferRequest request) {
        List<Transfer> transfers = transferRepository.findByBatchId(request.getBatchId());
        if (transfers.isEmpty()) {
            throw new ZarnabException(ExceptionType.TRANSFER_NOT_FOUND);
        }

        Transfer transfer = transfers.getFirst();

        otpService.verifyOtp(OtpPurpose.INGOT_TRANSFER, transfer.getSeller().getMobileNumber(), request.getSenderVerificationCode());

        if (transfer.getStatus() != TransferStatus.PENDING_SENDER_VERIFICATION) {
            throw new ZarnabException(ExceptionType.TRANSFER_INVALID_STATUS);
        }

    }

    @Transactional
    @Override
    public VerifyTransferResponse verifySender(VerifyTransferRequest request) {
        List<Transfer> transfers = transferRepository.findByBatchId(request.getBatchId());
        if (transfers.isEmpty()) {
            throw new ZarnabException(ExceptionType.TRANSFER_NOT_FOUND);
        }

        Transfer transfer = transfers.getFirst();
        otpService.verifyOtp(OtpPurpose.INGOT_TRANSFER, transfer.getSeller().getMobileNumber(),
                request.getSenderVerificationCode());

        if (transfer.getStatus() != TransferStatus.PENDING_SENDER_VERIFICATION) {
            throw new ZarnabException(ExceptionType.TRANSFER_INVALID_STATUS);
        }

        Optional<User> buyerOptional = userRepository.findByMobileNumber(request.getReceiverMobileNumber());
        boolean toCustomer = request.getTo() == InitiateTransferRequest.TransferTarget.CUSTOMER;
        boolean toZarnab = request.getTo() == InitiateTransferRequest.TransferTarget.ZARNAB;
        if (toCustomer || (!toZarnab && (buyerOptional.isEmpty() || RoleUtil.hasActiveRole(buyerOptional.get(), Role.CUSTOMER)))) {
            if (request.getReceiverMobileNumber() != null)
                smsService.sendSms(request.getReceiverMobileNumber(), translate("transfer.receiver.receiveIngotNotification", transfer.getIngot().getSerial()));
            transfers.forEach(t -> {
                t.setStatus(TransferStatus.PENDING_RECEIVER_VERIFICATION);
                t.setBuyer(buyerOptional.orElse(null));
            });
            transferRepository.saveAll(transfers);
            return new VerifyTransferResponse(TransferStatus.PENDING_RECEIVER_VERIFICATION);
        } else {
            List<Ingot> list = transfers.stream().map(Transfer::getIngot)
                    .peek(ingot -> {
                        ingot.setOwner(toZarnab ? null : buyerOptional.get());
                    }).toList();
            ingotRepository.saveAll(list);
            transfers.forEach(t -> {
                t.setBuyer(toZarnab ? null : buyerOptional.get());
                t.setStatus(TransferStatus.COMPLETED);
            });
            transferRepository.saveAll(transfers);
            smsService.sendSms(transfer.getBuyerMobileNumber(), translate("transfer.success", transfer.getBuyerMobileNumber()));
            return new VerifyTransferResponse(TransferStatus.COMPLETED);
        }

    }

    @Override
    public void receiverAction(Long transferId, boolean isApproved, User currentUser) {

        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new ZarnabException(ExceptionType.TRANSFER_NOT_FOUND, transferId));

        if (!transfer.getBuyerMobileNumber().equals(currentUser.getMobileNumber())) {
            throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER);
        }

        Ingot ingot = transfer.getIngot();
        ingot.setOwner(currentUser);
        ingotRepository.save(ingot);

        transfer.setStatus(isApproved ? TransferStatus.COMPLETED : TransferStatus.CANCELED);
        transferRepository.save(transfer);
    }

    @Override
    @Transactional
    public VerifyTransferResponse setReceiver(SetReceiverRequest request) {
        List<Transfer> transfers = transferRepository.findByBatchId(request.getBatchId());
        if (transfers.isEmpty()) {
            throw new ZarnabException(ExceptionType.TRANSFER_NOT_FOUND);
        }

        Transfer transfer = transfers.getFirst();

        User owner = transfer.getIngot().getOwner();
        if (owner != null && owner.getMobileNumber().equals(request.getReceiverMobileNumber())) {
            throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER);
        }

        if (transfer.getStatus() != TransferStatus.PENDING_SENDER_VERIFICATION) {
            throw new ZarnabException(ExceptionType.TRANSFER_INVALID_STATUS);
        }

        transfer.setBuyerMobileNumber(request.getReceiverMobileNumber());

        Optional<User> buyerOptional = userRepository.findByMobileNumber(transfer.getBuyerMobileNumber());
        if (buyerOptional.isEmpty() || RoleUtil.hasActiveRole(buyerOptional.get(), Role.CUSTOMER)) {
            transfer.setStatus(TransferStatus.PENDING_RECEIVER_VERIFICATION);
            smsService.sendSms(request.getReceiverMobileNumber(), translate("transfer.receiver.receiveIngotNotification", transfer.getIngot().getSerial()));
            transferRepository.save(transfer);
            return new VerifyTransferResponse(TransferStatus.PENDING_RECEIVER_VERIFICATION);
        } else {
            transfer.setBuyer(buyerOptional.get());
            transfer.setStatus(TransferStatus.COMPLETED);
            Ingot ingot = transfer.getIngot();
            ingot.setOwner(buyerOptional.get());
            ingotRepository.save(ingot);
            transferRepository.save(transfer);
            smsService.sendSms(transfer.getBuyerMobileNumber(), translate("transfer.success", transfer.getBuyerMobileNumber()));
            return new VerifyTransferResponse(TransferStatus.COMPLETED);
        }
    }

    @Transactional
    @Override
    public void verifyReceiver(VerifyQuickTransferRequest request) {
        List<Transfer> transfers = transferRepository.findByBatchId(request.getBatchId());
        if (transfers.isEmpty()) {
            throw new ZarnabException(ExceptionType.TRANSFER_NOT_FOUND);
        }
        Transfer transfer = transfers.getFirst();

        otpService.verifyOtp(OtpPurpose.INGOT_TRANSFER, transfer.getBuyerMobileNumber(), request.getReceiverVerificationCode());

        if (transfer.getStatus() != TransferStatus.PENDING_RECEIVER_VERIFICATION) {
            throw new ZarnabException(ExceptionType.TRANSFER_INVALID_STATUS);
        }

        User buyer = userRepository.findByMobileNumber(transfer.getBuyerMobileNumber())
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));

        for (Transfer t : transfers) {
            t.setBuyer(buyer);
            t.setStatus(TransferStatus.COMPLETED);
            Ingot ingot = t.getIngot();
            ingot.setOwner(buyer);
            ingotRepository.save(ingot);
            transferRepository.save(t);
        }

        smsService.sendSms(transfer.getBuyerMobileNumber(), translate("transfer.success", transfer.getBuyerMobileNumber()));
    }

    private void validateTransfer(User seller, String buyerMobileNumber, List<Ingot> ingots, InitiateTransferRequest.TransferTarget to) {
        boolean sellerIsAdmin = RoleUtil.hasActiveRole(seller, Role.ADMIN);
        boolean sellerIsCounter = RoleUtil.hasActiveRole(seller, Role.COUNTER);
        boolean sellerIsCustomer = !sellerIsAdmin && !sellerIsCounter;

        User buyer = null;

        if (sellerIsAdmin) {
            if (to != InitiateTransferRequest.TransferTarget.COUNTER) {
                throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER);
            }
        } else if (sellerIsCustomer) {
            if (to != InitiateTransferRequest.TransferTarget.CUSTOMER && to != InitiateTransferRequest.TransferTarget.COUNTER) {
                throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER);
            }
        } else { // sellerIsCounter
            if (to != InitiateTransferRequest.TransferTarget.CUSTOMER && to != InitiateTransferRequest.TransferTarget.ZARNAB) {
                throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER);
            }
        }

        if (to == InitiateTransferRequest.TransferTarget.CUSTOMER || to == InitiateTransferRequest.TransferTarget.COUNTER) {
            if (buyerMobileNumber == null) {
                throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER);
            }
            var buyerOptional = userRepository.findByMobileNumber(buyerMobileNumber);
//                    .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));

            if (to == InitiateTransferRequest.TransferTarget.COUNTER && (buyerOptional.isEmpty() || !(RoleUtil.hasActiveRole(buyerOptional.get(), Role.COUNTER)))) {
                throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER);
            }

            if (to == InitiateTransferRequest.TransferTarget.CUSTOMER && buyerOptional.isPresent() && !(RoleUtil.hasActiveRole(buyerOptional.get(), Role.CUSTOMER))) {
                throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER);
            }
            buyer = buyerOptional.orElse(null);
        }

        if ((sellerIsCustomer || (sellerIsCounter && to != InitiateTransferRequest.TransferTarget.ZARNAB)) && ingots.size() > 1) {
            throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER, "Cannot transfer more than one ingot at a time.");
        }

        for (Ingot ingot : ingots) {
            // in old
            //            if (!sellerIsAdmin && !ingot.getOwner().getId().equals(seller.getId())) {
            if (!sellerIsAdmin && (ingot.getOwner() == null || !ingot.getOwner().getId().equals(seller.getId()))) {
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

        if (transfer.getStatus() != TransferStatus.PENDING_SENDER_VERIFICATION && transfer.getStatus() != TransferStatus.PENDING_RECEIVER_VERIFICATION) {
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

        boolean isAdminOrCounter = RoleUtil.hasActiveRole(user, Role.ADMIN, Role.COUNTER);
        if (!isAdminOrCounter) {
            Specification<Transfer> userSecuritySpec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("seller"), user),
//                            criteriaBuilder.equal(root.get("buyerMobileNumber"), user),
                            criteriaBuilder.equal(root.get("buyerMobileNumber"), user.getMobileNumber())
                    );
            spec = (spec == null) ? userSecuritySpec : spec.and(userSecuritySpec);
        }

        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), pageableRequest.getSort());

        Page<Transfer> transferPage = transferRepository.findAll(spec, pageable);

        List<IngotDtos.TransferDto> transferDtos = transferPage.getContent().stream()
                .map(transfer -> IngotDtos.TransferDto.from(transfer, user))
                .collect(Collectors.toList());

        return new PageableResponse<>(
                transferDtos,
                transferPage.getTotalElements(),
                transferPage.getNumber(),
                transferPage.getSize()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public PageableResponse<IngotDtos.TransferDto> getCounterTransfers(User user, PageableRequest pageableRequest) {

        if (!RoleUtil.hasActiveRole(user, Role.ADMIN)) {
            return null;
        }
        pageableRequest.addToAliases("serial", "ingot.serial");

        Specification<Transfer> spec = SpecificationBuilder.buildSpecification(pageableRequest);

        Specification<Transfer> counterSpec = (root, query, cb) -> {

            Subquery<String> sub = query.subquery(String.class);
            Root<User> userRoot = sub.from(User.class);
            Join<User, Role> rolesJoin = userRoot.join("roles");

            sub.select(userRoot.get("mobileNumber"))
                    .where(cb.equal(rolesJoin, Role.COUNTER));

            return root.get("buyerMobileNumber").in(sub);
        };
        spec = (spec == null) ? counterSpec : spec.and(counterSpec);

        Pageable pageable = PageRequest.of(
                pageableRequest.getPage(),
                pageableRequest.getSize(),
                pageableRequest.getSort()
        );

        Page<Transfer> transferPage = transferRepository.findAll(spec, pageable);

        List<IngotDtos.TransferDto> transferDtos = transferPage.getContent()
                .stream()
                .map(transfer -> IngotDtos.TransferDto.from(transfer, null))
                .toList();

        return new PageableResponse<>(
                transferDtos,
                transferPage.getTotalElements(),
                transferPage.getNumber(),
                transferPage.getSize()
        );
    }


    @Transactional(readOnly = true)
    @Override
    public List<MonthlyWeightDashboard> getMonthlyCounterToUserTransfers(User user) throws AccessDeniedException {
        if (!RoleUtil.hasActiveRole(user, Role.ADMIN)) {
            throw new AccessDeniedException("Only admins can access this data");
        }
        AtomicReference<Double> runningWeight = new AtomicReference<>(0.0);
        return transferRepository.getMonthlyAssigned()
                .stream()
                .sorted(Comparator.comparing(MonthlyWeightDashboard::getDate))
                .peek(item -> {
                    double currentCumulative = runningWeight.accumulateAndGet(
                            item.getTotalWeight(),
                            Double::sum
                    );
                    item.setTotalWeight(currentCumulative);
                    item.setTotalPrice(currentCumulative * goldPrice);
                })
                .toList();
    }

}
