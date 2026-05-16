package com.zarnab.panel.ingot.repository;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.ingot.dto.res.IngotPurityStatsDto;
import com.zarnab.panel.ingot.model.Ingot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IngotRepository extends JpaRepository<Ingot, Long>, JpaSpecificationExecutor<Ingot> {

    @EntityGraph(attributePaths = {"owner.naturalPersonProfile.firstName", "owner.naturalPersonProfile.lastName", "owner.id"})
    List<Ingot> findByOwnerId(Long ownerId);

    @Override
    Page<Ingot> findAll(Specification<Ingot> spec, Pageable pageable);

    Optional<Ingot> findBySerial(String serial);

    boolean existsBySerial(String serial);

    List<Ingot> findByOwnerIdOrOwnerIdIsNull(Long id);

    Optional<Ingot> findTopBymanufactureDateBetweenOrderByIdDesc(LocalDate startDate, LocalDate endDate);

    List<Ingot> findAllBySerialIn(List<String> serials);

    List<Ingot> findByBatchIdOrderByIdAsc(Long batchId);

    @Query("""
       SELECT
           i.karat as karat,
           SUM(i.weightGrams) as totalWeight,
           COUNT(i.id) as count
       FROM Ingot i
       WHERE i.state = 'ASSIGNED'
       GROUP BY i.karat
       ORDER BY i.karat
       """)
    List<IngotPurityStatsDto> getIngotsGroupedByPurity();

    @Query("""
       SELECT 
           i.karat as karat,
           SUM(i.weightGrams) as totalWeight,
           COUNT(i.id) as count
       FROM Ingot i
       WHERE i.owner = :user AND i.state = 'ASSIGNED'
       GROUP BY i.karat
       ORDER BY i.karat
       """)
    List<IngotPurityStatsDto> getIngotsGroupedByPurityForUser(User user);
}
