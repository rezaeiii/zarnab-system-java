package com.zarnab.panel.profile.dto;

import org.springframework.web.multipart.MultipartFile;

public class ProfileDtos {

    public record InitiateChangeMobileRequest(
            String newMobileNumber
    ) {
    }

    public record VerifyChangeMobileRequest(
            String newMobileNumber,
            String otp
    ) {
    }

    public record UpdateProfileRequest(
            String firstName,
            String lastName
    ) {
    }
}
