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
    public void initiateTransfer(InitiateTransferRequest request, String username) {
        User seller = userRepository.findByMobileNumber(username)
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));
        Ingot ingot = ingotRepository.findById(request.getIngotId())
                .orElseThrow(() -> new ZarnabException(ExceptionType.INGOT_NOT_FOUND));

        if (!ingot.getOwner().equals(seller)) {
            throw new ZarnabException(ExceptionType.INGOT_OWNERSHIP_ERROR);
        }

        otpService.sendOtp(OtpPurpose.INGOT_TRANSFER, request.getBuyerMobileNumber());

        Transfer transfer = Transfer.builder()
                .ingot(ingot)
                .seller(seller)
                .buyerMobileNumber(request.getBuyerMobileNumber())
                .status(TransferStatus.PENDING)
                .build();

        transferRepository.save(transfer);
    }

    @Override
    @Transactional
    public void verifyAndCompleteTransfer(VerifyTransferRequest request, String username) {
        User seller = userRepository.findByMobileNumber(username)
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));
        Transfer transfer = transferRepository.findById(request.getTransferId())
                .orElseThrow(() -> new ZarnabException(ExceptionType.TRANSFER_NOT_FOUND));

        if (!transfer.getSeller().equals(seller)) {
            throw new ZarnabException(ExceptionType.TRANSFER_SELLER_MISMATCH);
        }

        otpService.verifyOtp(OtpPurpose.INGOT_TRANSFER, transfer.getBuyerMobileNumber(), request.getVerificationCode());

        transfer.setStatus(TransferStatus.VERIFIED);

        User buyer = userRepository.findByMobileNumber(transfer.getBuyerMobileNumber()).orElse(null);

        if (buyer != null) {
            transfer.setBuyer(buyer);
        }

        Ingot ingot = transfer.getIngot();
        ingot.setOwner(buyer);
        ingotRepository.save(ingot);

        transfer.setStatus(TransferStatus.COMPLETED);
        transferRepository.save(transfer);

        smsService.sendSms(transfer.getBuyerMobileNumber(), "You have received an ingot");
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
                .map(IngotDtos.TransferDto::from)
                .collect(Collectors.toList());
    }
}
