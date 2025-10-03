package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.auth.service.otp.OtpPurpose;
import com.zarnab.panel.auth.service.otp.OtpService;
import com.zarnab.panel.auth.service.sms.SmsService;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.req.InitiateTransferRequest;
import com.zarnab.panel.ingot.dto.req.VerifyTransferRequest;
import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.model.Transfer;
import com.zarnab.panel.ingot.model.TransferStatus;
import com.zarnab.panel.ingot.repository.IngotRepository;
import com.zarnab.panel.ingot.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final IngotRepository ingotRepository;
    private final UserRepository userRepository;
    private final TransferRepository transferRepository;
    private final OtpService otpService;
    private final SmsService smsService;

    @Override
    @Transactional
    public Long initiateTransfer(InitiateTransferRequest request, String username) {
        User seller = userRepository.findByMobileNumber(username)
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));
        Ingot ingot = ingotRepository.findById(request.getIngotId())
                .orElseThrow(() -> new ZarnabException(ExceptionType.INGOT_NOT_FOUND));

        if (!ingot.getOwner().equals(seller)) {
            throw new ZarnabException(ExceptionType.INGOT_OWNERSHIP_ERROR);
        }

        // Send OTP to the buyer for the seller to use for verification
        otpService.sendOtp(OtpPurpose.INGOT_TRANSFER, request.getBuyerMobileNumber());

        Transfer transfer = Transfer.builder()
                .ingot(ingot)
                .seller(seller)
                .buyerMobileNumber(request.getBuyerMobileNumber())
                .status(TransferStatus.PENDING_SELLER_VERIFICATION)
                .build();
        Transfer save = transferRepository.save(transfer);

        return save.getId();
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

        if (!transfer.getSeller().equals(seller)) {
            throw new ZarnabException(ExceptionType.TRANSFER_SELLER_MISMATCH);
        }

        // The seller verifies using the OTP sent to the buyer
        otpService.verifyOtp(OtpPurpose.INGOT_TRANSFER, transfer.getBuyerMobileNumber(), request.getVerificationCode());

        User buyer = userRepository.findByMobileNumber(transfer.getBuyerMobileNumber())
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND)); // Or create a new user if they don't exist

        transfer.setBuyer(buyer);
        transfer.setStatus(TransferStatus.COMPLETED);

        Ingot ingot = transfer.getIngot();
        ingot.setOwner(buyer);
        ingotRepository.save(ingot);

        smsService.sendSms(transfer.getBuyerMobileNumber(), "An ingot has been successfully transferred to you.");

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

        List<Transfer> transfers;
        if (user.getRoles().stream().anyMatch(role -> role.equals(Role.ADMIN))) {
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
