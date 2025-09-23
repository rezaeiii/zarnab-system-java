package com.zarnab.panel.auth.dto.req;

import com.zarnab.panel.auth.service.otp.OtpPurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record GenericVerifyOtpRequest(
        @NotNull
        OtpPurpose purpose,
        @NotBlank
        @Schema(example = "09999999999")
        @Pattern(regexp = "^09\\d{9}$", message = "Mobile number must be in this format (e.g., 09123456789).")
        String mobileNumber,
        @Pattern(
                regexp = "^\\d{6}$",
                message = "otp code must be 6 digits (e.g., 123456)."
        )
        @NotBlank
        String otp
) {
}