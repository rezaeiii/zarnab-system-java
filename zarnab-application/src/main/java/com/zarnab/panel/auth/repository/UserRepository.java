package com.zarnab.panel.auth.repository;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.dashboard.dto.DashboardStatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByMobileNumber(String mobileNumber);

    Boolean existsByMobileNumber(String mobileNumber);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.naturalPersonProfile.nationalId = :nationalId")
    boolean existsByNationalId(String nationalId);

    long countByCreatedAtAfter(LocalDateTime createdAt);

    @Query("SELECT u FROM User u WHERE u.mobileNumber = :mobileNumber OR u.naturalPersonProfile.nationalId = :nationalId")
    Optional<User> findByNationalIdOrMobileNumber(String mobileNumber, String nationalId);

    @Query("""
        SELECT new com.zarnab.panel.dashboard.dto.DashboardStatsDto(
            (SELECT count(u.id) FROM User u join u.roles r where r = "CUSTOMER"),
            (SELECT count(u2.id) FROM User u2 join u2.roles r where r = "CUSTOMER" AND u2.createdAt > :lastMonth),

            (SELECT COALESCE(sum(i.weightGrams), 0.0) FROM Ingot i WHERE i.state = "ASSIGNED"),

            (SELECT COALESCE(sum(i2.weightGrams), 0.0)
             FROM Ingot i2 JOIN i2.owner o join o.roles r
             WHERE r = "CUSTOMER"),
            (SELECT count(t.id) FROM Transfer t JOIN t.buyer.roles br JOIN t.seller.roles sr Where (br = "COUNTER" and sr = "CUSTOMER") or br = "CUSTOMER" ),
            (SELECT count(t.id) FROM Transfer t JOIN t.buyer.roles br JOIN t.seller.roles sr Where (br = "COUNTER" and sr = "CUSTOMER") ),
            (SELECT count(t.id) FROM Transfer t JOIN t.buyer.roles br JOIN t.seller.roles sr Where br = "CUSTOMER" ),
            (SELECT count(t2.id)
             FROM Transfer t2 JOIN t2.buyer.roles br JOIN t2.seller.roles sr
             WHERE t2.createdAt > :lastMonth and (br = "COUNTER" and sr = "CUSTOMER") or br = "CUSTOMER"),
            (SELECT count(i3.id)
             FROM Ingot i3
             WHERE SUBSTRING(i3.serial,1,1) = 'Z' and i3.state = "ASSIGNED"),
            (SELECT count(i4.id)
             FROM Ingot i4
             WHERE SUBSTRING(i4.serial,1,1) = 'Y' and i4.state = "ASSIGNED"),
            (SELECT count(i5.id)
             FROM Ingot i5
             WHERE SUBSTRING(i5.serial,1,1) = 'X' and i5.state = "ASSIGNED"),
            (SELECT count(i6.id)
             FROM Ingot i6
             WHERE SUBSTRING(i6.serial,1,1) = 'W' and i6.state = "ASSIGNED"),
            (SELECT COALESCE(sum(i7.weightGrams), 0.0)
             FROM Ingot i7
             WHERE SUBSTRING(i7.serial,1,1) IN ('C','E','G','I','K','M','O','Q') and i7.state = "ASSIGNED"),
            (SELECT COALESCE(sum(i8.weightGrams), 0.0)
             FROM Ingot i8
             WHERE SUBSTRING(i8.serial,1,1) IN ('R','S','T','U') and i8.state = "ASSIGNED")
        )
        """)
    DashboardStatsDto getDashboardStats(@Param("lastMonth") LocalDateTime lastMonth);

    @Query("""
        SELECT new com.zarnab.panel.dashboard.dto.DashboardStatsDto(
            1L,
            0L,
            (SELECT COALESCE(sum(i.weightGrams), 0.0)
             FROM Ingot i
             WHERE i.owner = :user),
            0.0,
            (SELECT count(t.id)
             FROM Transfer t
             WHERE t.seller = :user OR t.buyer = :user),
            (SELECT count(t.id)
             FROM Transfer t
             WHERE t.buyer = :user),
            (SELECT count(t.id)
             FROM Transfer t
             WHERE t.seller = :user),
            (SELECT count(t2.id)
             FROM Transfer t2
             WHERE (t2.seller = :user OR t2.buyer = :user)
             AND t2.createdAt > :lastMonth),
            (SELECT count(i3.id)
             FROM Ingot i3
             WHERE i3.owner = :user
             AND SUBSTRING(i3.serial,1,1) = 'Z'),
            (SELECT count(i4.id)
             FROM Ingot i4
             WHERE i4.owner = :user
             AND SUBSTRING(i4.serial,1,1) = 'Y'),
            (SELECT count(i5.id)
             FROM Ingot i5
             WHERE i5.owner = :user
             AND SUBSTRING(i5.serial,1,1) = 'X'),
            (SELECT count(i6.id)
             FROM Ingot i6
             WHERE i6.owner = :user
             AND SUBSTRING(i6.serial,1,1) = 'W'),
            (SELECT COALESCE(sum(i7.weightGrams), 0.0)
             FROM Ingot i7
             WHERE i7.owner = :user
             AND SUBSTRING(i7.serial,1,1) IN ('C','E','G','I','K','M','O','Q')),
            (SELECT COALESCE(sum(i8.weightGrams), 0.0)
             FROM Ingot i8
             WHERE i8.owner = :user
             AND SUBSTRING(i8.serial,1,1) IN ('R','S','T','U'))
        )
        FROM User u
        WHERE u.id = :userId
    """)
    DashboardStatsDto getCustomerDashboardStats(@Param("user") User user, @Param("lastMonth") LocalDateTime lastMonth);

}
