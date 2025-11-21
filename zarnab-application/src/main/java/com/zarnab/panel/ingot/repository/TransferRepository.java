package com.zarnab.panel.ingot.repository;

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
import java.util.Set;

public interface TransferRepository extends JpaRepository<Transfer, Long>, JpaSpecificationExecutor<Transfer> {

    List<Transfer> findAllByStatusInAndCreatedAtBefore(Collection<TransferStatus> statuses, LocalDateTime createdAt);

    List<Transfer> findByBatchId(String batchId);

    @Query("select t.ingot.id from Transfer t where t.ingot.batch.id = :batchId")
    Set<Long> findTransferredIngotIdsByBatchId(@Param("batchId") Long batchId);

    @Query("from Transfer where seller.id = :sellerId and ingot.id = :ingotId and status = :transferStatus")
    Optional<Transfer> findBySellerAndIngotAndStatus(@Param("sellerId") Long sellerId, @Param("ingotId") Long ingotId, TransferStatus transferStatus);

    @Query("from Transfer where seller.id = :sellerId and ingot.id in(:ingotIds)  and status in(:statuses)")
    List<Transfer> findBySellerAndIngotAndStatusIn(@Param("sellerId") Long sellerId,
                                                   @Param("ingotIds") Set<Long> ingotIds,
                                                   @Param("statuses") List<TransferStatus> statues);

    boolean existsByIngotId(Long id);
}
