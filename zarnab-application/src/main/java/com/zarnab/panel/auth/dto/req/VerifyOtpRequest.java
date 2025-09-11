package com.zarnab.panel.auth.dto.req;

import jakarta.validation.constraints.NotBlank;

public record VerifyOtpRequest(@NotBlank String mobileNumber, @NotBlank String otp) {}
