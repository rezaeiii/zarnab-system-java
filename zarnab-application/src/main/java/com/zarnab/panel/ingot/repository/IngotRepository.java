package com.zarnab.panel.ingot.repository;

import com.zarnab.panel.ingot.model.Ingot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngotRepository extends JpaRepository<Ingot, Long> {

    @EntityGraph(attributePaths = {"owner.naturalPersonProfile.firstName", "owner.naturalPersonProfile.lastName", "owner.id"})
    List<Ingot> findByOwnerId(Long ownerId);

    Optional<Ingot> findBySerial(String serial);

}
