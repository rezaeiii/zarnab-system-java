package com.zarnab.panel.ingot.repository;

import com.zarnab.panel.ingot.model.Ingot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IngotRepository extends JpaRepository<Ingot, Long>, JpaSpecificationExecutor<Ingot> {

    @EntityGraph(attributePaths = {"owner.naturalPersonProfile.firstName", "owner.naturalPersonProfile.lastName", "owner.id"})
    List<Ingot> findByOwnerId(Long ownerId);

    Optional<Ingot> findBySerial(String serial);

    boolean existsBySerial(String serial);

    List<Ingot> findByOwnerIdOrOwnerIdIsNull(Long id);

    Optional<Ingot> findTopBymanufactureDateBetweenOrderBySerialDesc(LocalDate startDate, LocalDate endDate);
}
