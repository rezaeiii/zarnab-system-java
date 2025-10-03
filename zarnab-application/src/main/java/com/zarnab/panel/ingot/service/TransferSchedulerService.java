package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.service.otp.OtpPurpose;
import com.zarnab.panel.ingot.model.Transfer;
import com.zarnab.panel.ingot.model.TransferStatus;
import com.zarnab.panel.ingot.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferSchedulerService {

    private final TransferRepository transferRepository;

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void expireOldTransfers() {
        log.info("Running scheduled job to expire old transfers...");

        long expirationSeconds = OtpPurpose.INGOT_TRANSFER.expirationSeconds();
        LocalDateTime expirationTime = LocalDateTime.now().minusSeconds(expirationSeconds);

        List<Transfer> expiredTransfers = transferRepository.findAllByStatusInAndCreatedAtBefore(
                List.of(TransferStatus.PENDING_SELLER_VERIFICATION),
                expirationTime
        );

        if (expiredTransfers.isEmpty()) {
            log.info("No expired transfers found.");
            return;
        }

        for (Transfer transfer : expiredTransfers) {
            log.info("Expiring transfer with ID: {}", transfer.getId());
            transfer.setStatus(TransferStatus.EXPIRED);
        }

        transferRepository.saveAll(expiredTransfers);
        log.info("Expired {} transfers.", expiredTransfers.size());
    }
}
