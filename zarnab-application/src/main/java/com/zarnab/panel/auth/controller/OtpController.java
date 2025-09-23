package com.zarnab.panel.auth.controller;

import com.zarnab.panel.auth.dto.req.GenericSendOtpRequest;
import com.zarnab.panel.auth.dto.req.GenericVerifyOtpRequest;
import com.zarnab.panel.auth.service.otp.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<Void> send(@Valid @RequestBody GenericSendOtpRequest request) {
        otpService.sendOtp(request.purpose(), request.mobileNumber());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@Valid @RequestBody GenericVerifyOtpRequest request) {
        otpService.verifyOtp(request.purpose(), request.mobileNumber(), request.otp());
        return ResponseEntity.ok().build();
    }
} 