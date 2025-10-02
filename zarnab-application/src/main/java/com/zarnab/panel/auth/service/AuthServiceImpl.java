package com.zarnab.panel.auth.service;

import com.zarnab.panel.auth.dto.LoginResult;
import com.zarnab.panel.auth.dto.UserManagementDtos;
import com.zarnab.panel.auth.dto.VerifyOtpResult;
import com.zarnab.panel.auth.dto.req.InitiateLoginRequest;
import com.zarnab.panel.auth.dto.req.RegisterRequest;
import com.zarnab.panel.auth.model.NaturalPersonProfileEmbeddable;
import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.model.UserProfileType;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.auth.service.otp.OtpPurpose;
import com.zarnab.panel.auth.service.otp.OtpService;
import com.zarnab.panel.auth.service.ratelimit.RateLimiter;
import com.zarnab.panel.auth.service.token.TokenStore;
import com.zarnab.panel.clients.service.ShahkarInquiryClient;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.common.file.service.FileStorageService;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.core.security.JwtService;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final TokenStore registrationTokenStore;
    private final RateLimiter rateLimiter;
    private final FileStorageService fileStorageService;
    private final ShahkarInquiryClient shahkarClient;

    @Value("${zarnab.security.jwt.registration-token-expiration-ms}")
    private long regTokenExpiration;

    private static final String REG_TOKEN_KEY_PREFIX = "reg_token:";

    @Override
    public void initiateLogin(InitiateLoginRequest request) {
        otpService.sendOtp(OtpPurpose.LOGIN_REGISTRATION, request.mobileNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public VerifyOtpResult verifyOtp(String mobileNumber, String otp) {
        rateLimiter.checkVerificationAttempt(mobileNumber);
        otpService.verifyOtp(OtpPurpose.LOGIN_REGISTRATION, mobileNumber, otp);

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
            throw new ZarnabException(ExceptionType.USER_ALREADY_EXISTS);
        }

        if (Boolean.FALSE.equals(shahkarClient.verifyMobileOwner(request.nationalId(), mobileNumber).block())) {
            throw new ZarnabException(ExceptionType.INVALID_MOBILE_NATIONAL_SHAHKAR);
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
                .build();
        userRepository.save(newUser);

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
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new BadCredentialsException("Expired or invalid refresh token");
        }

        return createLoginResult(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserManagementDtos.UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(UserManagementDtos.UserResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserManagementDtos.UserResponse createUser(UserManagementDtos.CreateUserRequest request) {
        if (userRepository.existsByMobileNumber(request.mobileNumber())) {
            throw new ZarnabException(ExceptionType.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .mobileNumber(request.mobileNumber())
                .enabled(true)
                .roles(request.roles())
                .profileType(UserProfileType.NATURAL)
                .naturalPersonProfile(NaturalPersonProfileEmbeddable.builder()
                        .firstName(request.firstName())
                        .lastName(request.lastName())
                        .nationalId(request.nationalId())
                        .build())
                .build();

        return UserManagementDtos.UserResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserManagementDtos.UserResponse updateUser(Long userId, UserManagementDtos.UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));

        user.setEnabled(request.enabled());
        user.setRoles(request.roles());
        user.getNaturalPersonProfile().setFirstName(request.firstName());
        user.getNaturalPersonProfile().setLastName(request.lastName());
        user.getNaturalPersonProfile().setNationalId(request.nationalId());

        return UserManagementDtos.UserResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ZarnabException(ExceptionType.USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }

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
