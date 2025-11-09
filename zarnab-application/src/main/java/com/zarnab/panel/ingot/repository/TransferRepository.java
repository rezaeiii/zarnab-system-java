package com.zarnab.panel.ingot.repository;

import com.zarnab.panel.ingot.model.Transfer;
import com.zarnab.panel.ingot.model.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long>, JpaSpecificationExecutor<Transfer> {

    List<Transfer> findAllByStatusInAndCreatedAtBefore(Collection<TransferStatus> statuses, LocalDateTime createdAt);

    List<Transfer> findByBatchId(String batchId);


}
