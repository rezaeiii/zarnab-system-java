package com.zarnab.panel.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VerifyOtpRequest(@NotBlank
                               @Schema(example = "09999999999")
                               @Pattern(regexp = "^09\\d{9}$", message = "Mobile number must be in E.164 format (e.g., 09123456789).")
                               String mobileNumber,

                               @NotBlank
                               @Size(min = 6, max = 6, message = "OTP must contain only digits.")
                               @Pattern(regexp = "^\\d{6}$", message = "OTP must contain only digits.")
                               String otp) {

}
