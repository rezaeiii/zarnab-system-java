package com.zarnab.panel.auth.service.otp;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@Profile(value = {"dev", "stage"})
public class StaticOtpGenerator implements OtpGenerator {

    @Override
    public String generate() {
        return "111111";
    }
}