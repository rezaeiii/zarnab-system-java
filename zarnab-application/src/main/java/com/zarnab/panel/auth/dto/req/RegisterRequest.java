package com.zarnab.panel.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

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
        String nationalId
) {
}
