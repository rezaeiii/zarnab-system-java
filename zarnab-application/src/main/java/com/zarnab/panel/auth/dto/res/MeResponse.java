package com.zarnab.panel.auth.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zarnab.panel.auth.model.NaturalPersonProfileEmbeddable;
import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.model.UserProfileType;
import com.zarnab.panel.common.file.annotation.MinioUrl;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MeResponse(
        @Schema(description = "User's database identifier")
        Long id,

        @Schema(description = "Registered mobile number")
        String mobileNumber,

        @Schema(description = "Whether the account is enabled")
        boolean enabled,

        @Schema(description = "Assigned roles")
        Set<Role> roles,

        @Schema(description = "Profile type for the user")
        UserProfileType profileType,

        // Natural Person Fields
        String firstName,
        String lastName,
        String nationalId,
        @MinioUrl(onlyDownload = false)
        String nationalCardImageUrl,
        String birthDate,
        String deathStatus,
        String fatherName,
        String gender,
        String officeCode,
        String officeName,
        String shenasnameSeri,
        String shenasnameSerial,
        String shenasnamehNumber,

        // Common Fields
        String address,
        String postalCode

) {
    public static MeResponse from(User user) {
        NaturalPersonProfileEmbeddable profile = user.getNaturalPersonProfile();

        return new MeResponse(
                user.getId(),
                user.getMobileNumber(),
                user.isEnabled(),
                user.getRoles(),
                user.getProfileType(),
                profile != null ? profile.getFirstName() : null,
                profile != null ? profile.getLastName() : null,
                profile != null ? profile.getNationalId() : null,
                profile != null ? profile.getNationalCardImageUrl() : null,
                profile != null ? profile.getBirthDate() : null,
                profile != null ? profile.getDeathStatus() : null,
                profile != null ? profile.getFatherName() : null,
                profile != null ? profile.getGender() : null,
                profile != null ? profile.getOfficeCode() : null,
                profile != null ? profile.getOfficeName() : null,
                profile != null ? profile.getShenasnameSeri() : null,
                profile != null ? profile.getShenasnameSerial() : null,
                profile != null ? profile.getShenasnamehNumber() : null,
                user.getAddress(),
                user.getPostalCode()
        );
    }
}
