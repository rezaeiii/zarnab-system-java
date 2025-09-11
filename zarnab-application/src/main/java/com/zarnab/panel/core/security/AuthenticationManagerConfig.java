package com.zarnab.panel.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class AuthenticationManagerConfig {

    private final OtpAuthenticationProvider otpAuthenticationProvider;

    @Bean
    public AuthenticationManager authenticationManager() {
        // The ProviderManager is the default implementation of AuthenticationManager
        return new ProviderManager(Collections.singletonList(otpAuthenticationProvider));
    }
}
