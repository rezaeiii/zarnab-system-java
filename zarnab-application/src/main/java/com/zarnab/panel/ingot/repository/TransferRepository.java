package com.zarnab.panel.ingot.repository;

import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.model.Transfer;
import com.zarnab.panel.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    Optional<Transfer> findByIngotAndSeller(Ingot ingot, User seller);

    List<Transfer> findAllBySellerOrBuyer(User seller, User buyer);
}
