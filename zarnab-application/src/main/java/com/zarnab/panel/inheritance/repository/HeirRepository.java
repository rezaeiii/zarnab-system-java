package com.zarnab.panel.inheritance.repository;

import com.zarnab.panel.inheritance.model.Heir;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HeirRepository extends JpaRepository<Heir, Long> {
    Optional<Heir> findByInheritanceCase_TrackingCodeAndNationalId(String trackingCode, String nationalId);

    @Query("SELECT h FROM Heir h WHERE h.inheritanceCase.trackingCode = :trackingCode AND h.requester = true")
    Optional<Heir> findRequesterHeir(String trackingCode);

    Optional<Heir> findByMobileNumber(String mobileNumber);

    Optional<Heir> findByNationalId(String nationalId);
}
