package com.zarnab.panel.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

/**
 * DTO for the request to register a new user after OTP verification.
 */
public record RegisterRequest(
        @Schema(description = "The short-lived token received from the /verify-otp endpoint.", requiredMode = Schema.RequiredMode.REQUIRED)
        String registrationToken,

        @Schema(description = "The user's first name.", example = "Zarnab", requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,

        @Schema(description = "The user's last name.", example = "Sample", requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName,

        @Schema(description = "The user's national ID code.", example = "0011223344", requiredMode = Schema.RequiredMode.REQUIRED)
        String nationalId,

        @Schema(description = "The user's full address.")
        @Nullable
        String address,

        @Schema(description = "The user's postal code.")
        @Nullable
        String postalCode,

        @Schema(description = "The user's birth date.")
        @Nullable
        String birthDate,

        @Schema(description = "The user's death status.")
        @Nullable
        String deathStatus,

        @Schema(description = "The user's father's name.")
        @Nullable
        String fatherName,

        @Schema(description = "The user's gender.")
        @Nullable
        String gender,

        @Schema(description = "The user's office code.")
        @Nullable
        String officeCode,

        @Schema(description = "The user's office name.")
        @Nullable
        String officeName,

        @Schema(description = "The user's shenasname seri.")
        @Nullable
        String shenasnameSeri,

        @Schema(description = "The user's shenasname serial.")
        @Nullable
        String shenasnameSerial,

        @Schema(description = "The user's shenasname number.")
        @Nullable
        String shenasnamehNumber
) {
}
