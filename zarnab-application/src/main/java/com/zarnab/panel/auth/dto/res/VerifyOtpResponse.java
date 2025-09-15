package com.zarnab.panel.auth.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * DTO for the response after verifying an OTP.
 * It informs the client whether the login was successful or if registration is the next step.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VerifyOtpResponse(
        @Schema(description = "The status of the verification process.",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Status status,

        @Schema(description = "Registration token")
        String registrationToken,

        LoginResponse loginResponse
) {

    public enum Status {LOGIN_SUCCESS, REGISTRATION_REQUIRED}

}
