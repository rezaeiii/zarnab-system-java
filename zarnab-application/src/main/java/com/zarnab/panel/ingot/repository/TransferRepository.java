package com.zarnab.panel.ingot.repository;

import com.zarnab.panel.ingot.dto.MonthlyWeight;
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

    @Query("""
        SELECT new com.zarnab.panel.ingot.dto.MonthlyWeight(
            YEAR(t.createdAt), MONTH(t.createdAt), SUM(i.weightGrams)
        )
        FROM Transfer t
        JOIN t.ingot i
        JOIN t.seller s
        JOIN s.roles sr
        JOIN t.buyer b
        JOIN b.roles br
        WHERE sr = 'COUNTER'
          AND br = 'CUSTOMER'
          AND t.status = 'COMPLETED'
        GROUP BY YEAR(t.createdAt), MONTH(t.createdAt)
        ORDER BY YEAR(t.createdAt) DESC, MONTH(t.createdAt) DESC
    """)
    List<MonthlyWeight> getMonthlyWeightTransferredFromCounterToUser();

    @Query("""
        SELECT new com.zarnab.panel.ingot.dto.MonthlyWeight(
            YEAR(i.manufactureDate), MONTH(i.manufactureDate), SUM(i.weightGrams)
        )
        FROM Ingot i
        WHERE i.state = 'ASSIGNED'
        GROUP BY YEAR(i.manufactureDate), MONTH(i.manufactureDate)
        ORDER BY YEAR(i.manufactureDate) ASC, MONTH(i.manufactureDate) ASC
    """)
    List<MonthlyWeight> getMonthlyAssigned();
}
