package com.zarnab.panel.core.security;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.auth.service.otp.OtpPurpose;
import com.zarnab.panel.auth.service.otp.OtpService;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.core.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * The core of our custom authentication logic.
 * This provider validates the OTP and, if successful, creates an authenticated token.
 */
@Component
@RequiredArgsConstructor
public class OtpAuthenticationProvider implements AuthenticationProvider {

    private final OtpService otpService;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String mobileNumber = authentication.getName();
        String submittedOtp = authentication.getCredentials().toString();

        otpService.verifyOtp(OtpPurpose.LOGIN_REGISTRATION, mobileNumber, submittedOtp);

        // If OTP is valid, load the user details
        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND, mobileNumber));

        // Create a fully authenticated token with user's authorities
        return new OtpAuthenticationToken(
                user.getMobileNumber(),
                null, // Clear credentials after authentication
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // This provider only supports our custom OtpAuthenticationToken
        return OtpAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
