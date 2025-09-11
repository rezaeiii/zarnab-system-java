//package com.zarnab.panel.auth.dto;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
//
//public record LoginRequest(
//        @NotBlank(message = "Mobile number cannot be blank.")
//        String mobileNumber,
//
//        @NotBlank(message = "OTP cannot be blank.")
//        @Size(min = 6, max = 6, message = "OTP must be 6 digits.")
//        String otp
//) {
//}
