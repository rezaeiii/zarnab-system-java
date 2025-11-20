package com.zarnab.panel.ingot.repository;

import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.model.Transfer;
import com.zarnab.panel.ingot.model.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long>, JpaSpecificationExecutor<Transfer> {

    List<Transfer> findAllByStatusInAndCreatedAtBefore(Collection<TransferStatus> statuses, LocalDateTime createdAt);

    List<Transfer> findByBatchId(String batchId);

    @Query("from Transfer where seller.id = :ownerId and ingot.id = :ingotId and status = :transferStatus")
    Optional<Transfer> findBySellerAndIngotAndStatus(@Param("ownerId") Long ownerId, @Param("ingotId") Long ingotId, TransferStatus transferStatus);
}
