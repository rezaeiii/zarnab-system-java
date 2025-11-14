package com.zarnab.panel.inheritance.repository;

import com.zarnab.panel.inheritance.model.InheritanceCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InheritanceCaseRepository extends JpaRepository<InheritanceCase, Long>, JpaSpecificationExecutor<InheritanceCase> {
    Optional<InheritanceCase> findByTrackingCode(String trackingCode);
    boolean existsByDeceasedUser_MobileNumber(String mobileNumber);
    Optional<InheritanceCase> findByDeceasedUser_NaturalPersonProfile_NationalId(String nationalId);
}
