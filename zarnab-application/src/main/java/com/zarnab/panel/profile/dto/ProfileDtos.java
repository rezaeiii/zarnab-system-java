package com.zarnab.panel.profile.dto;

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
}
