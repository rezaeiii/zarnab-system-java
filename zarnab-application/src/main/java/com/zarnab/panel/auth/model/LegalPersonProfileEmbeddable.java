package com.zarnab.panel.auth.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalPersonProfileEmbeddable {
    private String companyName;
    private String economicCode;
    private String registrationNumber;
    private LocalDate establishmentDate;
    private String establishmentNoticeImageUrl;
    private String registrationLicenseImageUrl;
}
