package com.zarnab.panel.auth.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.model.UserProfileType;
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

        @Schema(description = "First name if natural person")
        String firstName,

        @Schema(description = "Last name if natural person")
        String lastName,

        @Schema(description = "National ID if natural person")
        String nationalId,

        @Schema(description = "National card image URL if provided")
        String nationalCardImageUrl,
        String postalCode,
        String address

) {
    public static MeResponse from(User user) {
        String firstName = user.getNaturalPersonProfile() != null ? user.getNaturalPersonProfile().getFirstName() : null;
        String lastName = user.getNaturalPersonProfile() != null ? user.getNaturalPersonProfile().getLastName() : null;
        String nationalId = user.getNaturalPersonProfile() != null ? user.getNaturalPersonProfile().getNationalId() : null;
        String nationalCardImageUrl = user.getNaturalPersonProfile() != null ? user.getNaturalPersonProfile().getNationalCardImageUrl() : null;

        return new MeResponse(
                user.getId(),
                user.getMobileNumber(),
                user.isEnabled(),
                user.getRoles(),
                user.getProfileType(),
                firstName,
                lastName,
                nationalId,
                nationalCardImageUrl,
                user.getPostalCode(),
                user.getAddress()
        );
    }
} 