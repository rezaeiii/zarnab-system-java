package com.zarnab.panel.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaturalPersonProfileEmbeddable {
    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String nationalId;
    private String nationalCardImageUrl;
    private String businessLicenseImageUrl;
}
