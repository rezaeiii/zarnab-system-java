package com.zarnab.panel.auth.service;

import com.zarnab.panel.auth.dto.LoginResult;
import com.zarnab.panel.auth.dto.VerifyOtpResult;
import com.zarnab.panel.auth.dto.req.InitiateLoginRequest;
import com.zarnab.panel.auth.dto.req.RegisterRequest;
import com.zarnab.panel.auth.model.NaturalPersonProfileEmbeddable;
import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.model.UserProfileType;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.auth.service.otp.OtpService;
import com.zarnab.panel.auth.service.ratelimit.RateLimiter;
import com.zarnab.panel.auth.service.token.TokenStore;
import com.zarnab.panel.common.file.service.FileStorageService;
import com.zarnab.panel.core.security.JwtService;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final TokenStore registrationTokenStore;
    private final RateLimiter rateLimiter;
    private final FileStorageService fileStorageService;

    @Value("${zarnab.security.jwt.registration-token-expiration-ms}")
    private long regTokenExpiration;

    private static final String REG_TOKEN_KEY_PREFIX = "reg_token:";

    @Override
    public void initiateLogin(InitiateLoginRequest request) {
        otpService.sendOtp(request.mobileNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public VerifyOtpResult verifyOtp(String mobileNumber, String otp) {
        rateLimiter.checkVerificationAttempt(mobileNumber);
        otpService.verifyOtp(mobileNumber, otp);

        return userRepository.findByMobileNumber(mobileNumber)
                .map(user -> new VerifyOtpResult(VerifyOtpResult.Status.LOGIN_SUCCESS, createLoginResult(user), null))
                .orElseGet(() -> new VerifyOtpResult(VerifyOtpResult.Status.REGISTRATION_REQUIRED, null, createRegistrationToken(mobileNumber)));
    }

    @Override
    @Transactional
    public LoginResult registerUser(RegisterRequest request, MultipartFile nationalIdImage) {
        String regTokenKey = REG_TOKEN_KEY_PREFIX + request.registrationToken();
        String mobileNumber = registrationTokenStore.consume(regTokenKey)
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired registration token."));

        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new UsernameNotFoundException("User with this mobile number already exists.");
        }

        // upload image
        ObjectWriteResponse imageObject = fileStorageService.uploadFile(nationalIdImage);

        User newUser = User.builder()
                .mobileNumber(mobileNumber)
                .enabled(true)
                .roles(Set.of(Role.USER, Role.REPRESENTATIVE))
                .profileType(UserProfileType.NATURAL)
                .naturalPersonProfile(NaturalPersonProfileEmbeddable.builder()
                        .firstName(request.firstName())
                        .lastName(request.lastName())
                        .nationalId(request.nationalId())
                        .nationalCardImageUrl(imageObject.object())

                        .build())
                // Profile mapping would happen here from request.firstName(), etc.
                .build();
        userRepository.save(newUser);

        // After registration, a login is performed automatically.
        return createLoginResult(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResult refreshTokens(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadCredentialsException("Missing refresh token");
        }
        String mobileNumber;
        try {
            mobileNumber = jwtService.extractMobileNumber(refreshToken);
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new BadCredentialsException("Expired or invalid refresh token");
        }

        return createLoginResult(user);
    }

    /**
     * Generates both access and refresh tokens for a given user.
     * This is the single source of truth for creating a login session.
     */
    private LoginResult createLoginResult(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new LoginResult(accessToken, refreshToken, user);
    }

    private String createRegistrationToken(String mobileNumber) {
        String regToken = UUID.randomUUID().toString();
        registrationTokenStore.store(REG_TOKEN_KEY_PREFIX + regToken, mobileNumber, regTokenExpiration, TimeUnit.MILLISECONDS);
        return regToken;
    }
}