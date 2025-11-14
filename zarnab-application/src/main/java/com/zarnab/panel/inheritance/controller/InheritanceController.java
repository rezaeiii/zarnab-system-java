package com.zarnab.panel.inheritance.controller;

import com.zarnab.panel.auth.service.otp.OtpPurpose;
import com.zarnab.panel.auth.service.otp.OtpService;
import com.zarnab.panel.clients.service.ShahkarInquiryClient;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.inheritance.dto.InheritanceDtos;
import com.zarnab.panel.inheritance.service.InheritanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/public/inheritance")
@RequiredArgsConstructor
public class InheritanceController {

    private final InheritanceService inheritanceService;
    private final ShahkarInquiryClient shahkarInquiryClient;
    private final OtpService otpService;

    @PostMapping("/initiate-otp")
    public ResponseEntity<Void> initiateOtp(@Valid @RequestBody InheritanceDtos.InitiateInheritanceOtpRequest request) {

        Boolean isMobileOwner = shahkarInquiryClient.verifyMobileOwner(request.nationalId(), request.mobileNumber()).block();
        if (isMobileOwner == null || !isMobileOwner) {
            throw new ZarnabException(ExceptionType.INVALID_MOBILE_NATIONAL_SHAHKAR);
        }
        otpService.sendOtp(OtpPurpose.INHERITANCE_VERIFICATION, request.mobileNumber());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check-status")
    public ResponseEntity<InheritanceDtos.CheckStatusResponse> checkStatus(@Valid @RequestBody InheritanceDtos.CheckStatusRequest request) {
        return ResponseEntity.ok(inheritanceService.checkStatus(request));
    }

    @PostMapping("/verify-death")
    public ResponseEntity<InheritanceDtos.DeathVerificationDetailResponse> verifyDeathStatus(@Valid @RequestBody InheritanceDtos.DeathVerificationRequest request) {
        return ResponseEntity.ok(inheritanceService.verifyDeathStatus(request));
    }

    @PostMapping("/initiate")
    public ResponseEntity<InheritanceDtos.InitiateResponse> initiateCase(@Valid @RequestBody InheritanceDtos.InitiateRequest request) {
        return ResponseEntity.ok(inheritanceService.initiateCase(request));
    }

    @PostMapping(value = "/{trackingCode}/upload-initiator", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadInitiatorDocuments(
            @PathVariable String trackingCode,
            @RequestPart("deathCertificate") MultipartFile deathCertificate,
            @RequestPart("inheritanceCertificate") MultipartFile inheritanceCertificate,
            @RequestPart("initiatorNationalId") MultipartFile initiatorNationalId) {
        inheritanceService.uploadInitiatorDocuments(trackingCode, deathCertificate, inheritanceCertificate, initiatorNationalId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-heir")
    public ResponseEntity<String> verifyHeir(@Valid @RequestBody InheritanceDtos.HeirVerificationRequest request) {
        return ResponseEntity.ok(inheritanceService.verifyHeir(request));
    }

    @PostMapping(value = "/{trackingCode}/heirs/{nationalId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadHeirDocument(
            @PathVariable String trackingCode,
            @PathVariable String nationalId,
            @RequestPart("nationalIdCard") MultipartFile nationalIdCard) {
        inheritanceService.uploadHeirDocument(trackingCode, nationalId, nationalIdCard);
        return ResponseEntity.ok().build();
    }
}
