package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.dto.UserManagementDtos;
import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.auth.service.otp.OtpPurpose;
import com.zarnab.panel.auth.service.otp.OtpService;
import com.zarnab.panel.auth.service.sms.SmsService;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.core.util.RoleUtil;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.req.InitiateTransferRequest;
import com.zarnab.panel.ingot.dto.req.VerifyTransferRequest;
import com.zarnab.panel.ingot.dto.res.InitiateTransferResponse;
import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.model.TheftReportStatus;
import com.zarnab.panel.ingot.model.Transfer;
import com.zarnab.panel.ingot.model.TransferStatus;
import com.zarnab.panel.ingot.repository.IngotRepository;
import com.zarnab.panel.ingot.repository.TheftReportRepository;
import com.zarnab.panel.ingot.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.zarnab.panel.common.translate.Translator.translate;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final IngotRepository ingotRepository;
    private final UserRepository userRepository;
    private final TransferRepository transferRepository;
    private final TheftReportRepository theftReportRepository;
    private final OtpService otpService;
    private final SmsService smsService;

    @Override
    @Transactional
    public InitiateTransferResponse initiateTransfer(InitiateTransferRequest request, String username) {

        User seller = userRepository.findByMobileNumber(username)
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));

        User buyer = null;
        if (request.getBuyerMobileNumber() != null && !request.getToZarnab()) {
            buyer = userRepository.findByMobileNumber(request.getBuyerMobileNumber())
                    .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));
            if (RoleUtil.hasRole(buyer, Role.ADMIN, Role.COUNTER)) {
                throw new ZarnabException(ExceptionType.INVALID_TRANSFER_BUYER);
            }
        }

        Ingot ingot = ingotRepository.findById(request.getIngotId())
                .orElseThrow(() -> new ZarnabException(ExceptionType.INGOT_NOT_FOUND));

        boolean isAdminOrCounter = RoleUtil.hasRole(seller, Role.ADMIN, Role.COUNTER);
        // if seller user is counter or admin dont need to check ownership
        if (!isAdminOrCounter
            && !ingot.getOwner().getId().equals(seller.getId())) {
            throw new ZarnabException(ExceptionType.INGOT_OWNERSHIP_ERROR);
        }

        if ((buyer == null && ingot.getOwner() == null) || (buyer != null
                                                            && ingot.getOwner() != null
                                                            && buyer.getId().equals(ingot.getOwner().getId()))) {
            String name = buyer != null
                    ? buyer.getNaturalPersonProfile().getFirstName() + " " + buyer.getNaturalPersonProfile().getLastName()
                    : "زرناب";
            throw new ZarnabException(ExceptionType.INGOT_ALREADY_OWNERSHIP, name);
        }

        if (theftReportRepository.existsByIngotAndStatusIn(ingot, List.of(TheftReportStatus.PENDING, TheftReportStatus.APPROVED))) {
            throw new ZarnabException(ExceptionType.INGOT_IS_STOLEN);
        }

        // Send OTP to the buyer for the seller to use for verification
        otpService.sendOtp(OtpPurpose.INGOT_TRANSFER, seller.getMobileNumber());

        Transfer transfer = Transfer.builder()
                .ingot(ingot)
                .seller(isAdminOrCounter ? null : seller)
                .buyerMobileNumber(request.getBuyerMobileNumber())
                .status(TransferStatus.PENDING_SELLER_VERIFICATION)
                .build();
        Transfer save = transferRepository.save(transfer);

        return new InitiateTransferResponse(save.getId(), UserManagementDtos.UserResponse.from(buyer));
    }

    @Override
    @Transactional
    public IngotDtos.TransferDto verifyTransfer(VerifyTransferRequest request, String username) {
        User seller = userRepository.findByMobileNumber(username)
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));
        Transfer transfer = transferRepository.findById(request.getTransferId())
                .orElseThrow(() -> new ZarnabException(ExceptionType.TRANSFER_NOT_FOUND));

        if (transfer.getStatus() != TransferStatus.PENDING_SELLER_VERIFICATION) {
            throw new ZarnabException(ExceptionType.TRANSFER_INVALID_STATUS);
        }

        boolean isAdminOrCounter = RoleUtil.hasRole(seller, Role.ADMIN, Role.COUNTER);

        if (!isAdminOrCounter && !transfer.getSeller().getId().equals(seller.getId())) {
            throw new ZarnabException(ExceptionType.TRANSFER_SELLER_MISMATCH);
        }

        // The seller verifies using the OTP sent to the buyer
        otpService.verifyOtp(OtpPurpose.INGOT_TRANSFER, seller.getMobileNumber(), request.getVerificationCode());
        User buyer = null;
        if (transfer.getBuyerMobileNumber() != null) {
            buyer = userRepository.findByMobileNumber(transfer.getBuyerMobileNumber())
                    .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));
        }

        transfer.setBuyer(buyer);
        transfer.setStatus(TransferStatus.COMPLETED);

        Ingot ingot = transfer.getIngot();
        ingot.setOwner(buyer);
        ingotRepository.save(ingot);

        if (transfer.getBuyerMobileNumber() != null)
            smsService.sendSms(transfer.getBuyerMobileNumber(), translate("transfer.success", transfer.getBuyerMobileNumber()));

        return IngotDtos.TransferDto.from(transferRepository.save(transfer));
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
    public List<IngotDtos.TransferDto> getTransfers(String username) {
        User user = userRepository.findByMobileNumber(username)
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));

        boolean isAdminOrCounter = RoleUtil.hasRole(user, Role.ADMIN, Role.COUNTER);
        List<Transfer> transfers;
        if (isAdminOrCounter) {
            transfers = transferRepository.findAll();
        } else {
            transfers = transferRepository.findAllBySellerOrBuyer(user, user);
        }

        return transfers.stream()
                .sorted(Comparator.comparing(Transfer::getCreatedAt, Comparator.reverseOrder()))
                .map(IngotDtos.TransferDto::from)
                .collect(Collectors.toList());
    }
}
