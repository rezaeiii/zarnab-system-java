package com.zarnab.panel.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * A command object representing the request to initiate a login.
 * Using a command object encapsulates the input parameters for a use case
 * and allows for declarative validation.
 *
 * @param mobileNumber The mobile number of the user trying to log in.
 */
public record InitiateLoginRequest(
        @NotBlank
        @Schema(example = "09999999999")
        @Pattern(regexp = "^09\\d{9}$", message = "Mobile number must be in E.164 format (e.g., 09123456789).")
        String mobileNumber
) {
}
