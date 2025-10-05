package com.zarnab.panel.auth.repository;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.dashboard.dto.DashboardStatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByMobileNumber(String mobileNumber);

    Boolean existsByMobileNumber(String mobileNumber);

    long countByCreatedAtAfter(LocalDateTime createdAt);

    @Query("""
            SELECT new com.zarnab.panel.dashboard.dto.DashboardStatsDto(
                (SELECT count(u.id) FROM User u),
                (SELECT count(u2.id) FROM User u2 WHERE u2.createdAt > :lastMonth),
                (SELECT COALESCE(sum(i.weightGrams), 0.0) FROM Ingot i),
                (SELECT COALESCE(sum(i2.weightGrams), 0.0) FROM Ingot i2 WHERE i2.owner IS NULL),
                (SELECT count(t.id) FROM Transfer t),
                (SELECT count(t2.id) FROM Transfer t2 WHERE t2.createdAt > :lastMonth)
            )
            """)
    DashboardStatsDto getDashboardStats(@Param("lastMonth") LocalDateTime lastMonth);

}
