package com.zarnab.panel.auth.dto.req;

import com.zarnab.panel.auth.service.otp.OtpPurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record GenericSendOtpRequest(
        @NotNull
        OtpPurpose purpose,
        @NotBlank
        @Schema(example = "09999999999")
        @Pattern(regexp = "^09\\d{9}$", message = "Mobile number must be in this format (e.g., 09123456789).")
        String mobileNumber
) {
}