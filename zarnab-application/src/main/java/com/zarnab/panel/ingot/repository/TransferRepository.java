package com.zarnab.panel.ingot.repository;

import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.model.Transfer;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.ingot.model.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> , JpaSpecificationExecutor<Transfer> {
    Optional<Transfer> findByIngotAndSeller(Ingot ingot, User seller);

    List<Transfer> findAllBySellerOrBuyer(User seller, User buyer);

    List<Transfer> findAllByStatusInAndCreatedAtBefore(Collection<TransferStatus> statuses, LocalDateTime createdAt);

    long countByCreatedAtAfter(LocalDateTime createdAt);

}
