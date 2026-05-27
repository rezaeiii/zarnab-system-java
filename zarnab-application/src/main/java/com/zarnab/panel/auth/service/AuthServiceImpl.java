package com.zarnab.panel.auth.service;

import com.zarnab.panel.auth.dto.LoginResult;
import com.zarnab.panel.auth.dto.UserManagementDtos;
import com.zarnab.panel.auth.dto.VerifyOtpResult;
import com.zarnab.panel.auth.dto.req.InitiateLoginRequest;
import com.zarnab.panel.auth.dto.req.RegisterRequest;
import com.zarnab.panel.auth.model.*;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.auth.service.otp.OtpPurpose;
import com.zarnab.panel.auth.service.otp.OtpService;
import com.zarnab.panel.auth.service.ratelimit.RateLimiter;
import com.zarnab.panel.auth.service.token.TokenStore;
import com.zarnab.panel.clients.service.ShahkarInquiryClient;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.common.file.service.FileStorageService;
import com.zarnab.panel.common.search.SpecificationBuilder;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.core.security.JwtService;
import com.zarnab.panel.core.util.RoleUtil;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;
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

        if (userRepository.existsByNationalId(request.nationalId())) {
            throw new ZarnabException(ExceptionType.NATIONAL_ID_ALREADY_EXISTS);
        }

        String imageUrl = null;
        if (nationalIdImage != null && !nationalIdImage.isEmpty()) {
            ObjectWriteResponse imageObject = fileStorageService.uploadFile(nationalIdImage);
            imageUrl = imageObject.object();
        }

        User newUser = User.builder()
                .mobileNumber(mobileNumber)
                .enabled(true)
                .roles(Set.of(Role.CUSTOMER))
                .profileType(UserProfileType.NATURAL)
                .accountStatus(AccountStatus.ACTIVE)
                .address(request.address())
                .postalCode(request.postalCode())
                .naturalPersonProfile(NaturalPersonProfileEmbeddable.builder()
                        .firstName(request.firstName())
                        .lastName(request.lastName())
                        .nationalId(request.nationalId())
                        .nationalCardImageUrl(imageUrl)
                        .birthDate(request.birthDate())
                        .deathStatus(request.deathStatus())
                        .fatherName(request.fatherName())
                        .gender(request.gender())
                        .officeCode(request.officeCode())
                        .officeName(request.officeName())
                        .shenasnameSeri(request.shenasnameSeri())
                        .shenasnameSerial(request.shenasnameSerial())
                        .shenasnamehNumber(request.shenasnamehNumber())
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
        String activeRole;
        try {
            mobileNumber = jwtService.extractMobileNumber(refreshToken);
            activeRole = jwtService.extractActiveRole(refreshToken);
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new BadCredentialsException("Expired or invalid refresh token");
        }

        return createLoginResult(user, Role.valueOf(activeRole));
    }

    @Override
    @Transactional(readOnly = true)
    public PageableResponse<UserManagementDtos.UserResponse> listUsers(PageableRequest pageableRequest) {
        pageableRequest.addToAliases("firstName", "naturalPersonProfile.firstName");
        pageableRequest.addToAliases("lastName", "naturalPersonProfile.lastName");
        pageableRequest.addToAliases("nationalId", "naturalPersonProfile.nationalId");

        Specification<User> spec = SpecificationBuilder.buildSpecification(pageableRequest);
        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), pageableRequest.getSort());

        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<UserManagementDtos.UserResponse> userDtos = userPage.getContent().stream()
                .map(UserManagementDtos.UserResponse::from)
                .collect(Collectors.toList());

        return new PageableResponse<>(
                userDtos,
                userPage.getTotalElements(),
                userPage.getNumber(),
                userPage.getSize()
        );
    }

    @Override
    @Transactional
    public UserManagementDtos.UserResponse createUser(UserManagementDtos.CreateUserRequest request) {
        if (userRepository.existsByMobileNumber(request.mobileNumber())) {
            throw new ZarnabException(ExceptionType.USER_ALREADY_EXISTS);
        }

        if (userRepository.existsByNationalId(request.nationalId())) {
            throw new ZarnabException(ExceptionType.NATIONAL_ID_ALREADY_EXISTS);
        }

        if (Boolean.FALSE.equals(shahkarClient.verifyMobileOwner(request.nationalId(), request.mobileNumber()).block())) {
            throw new ZarnabException(ExceptionType.INVALID_MOBILE_NATIONAL_SHAHKAR);
        }

        User user = User.builder()
                .mobileNumber(request.mobileNumber())
                .enabled(true)
                .roles(request.roles())
                .profileType(UserProfileType.NATURAL)
                .accountStatus(AccountStatus.ACTIVE)
                .naturalPersonProfile(NaturalPersonProfileEmbeddable.builder()
                        .firstName(request.firstName())
                        .lastName(request.lastName())
                        .nationalId(request.nationalId())
                        .build())
                .build();

        return UserManagementDtos.UserResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserManagementDtos.UserResponse loadUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return UserManagementDtos.UserResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User loadUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }

    @Override
    @Transactional
    public UserManagementDtos.UserResponse updateUser(Long userId, UserManagementDtos.UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));
        boolean mobileChanged = !Objects.equals(user.getMobileNumber(), request.mobileNumber());
        boolean nationalIdChanged = !Objects.equals(user.getNaturalPersonProfile().getNationalId(), request.nationalId());
        if (mobileChanged) {
            if (userRepository.existsByMobileNumber(request.mobileNumber())) {
                throw new ZarnabException(ExceptionType.USER_ALREADY_EXISTS);
            }
        }

        if (nationalIdChanged) {
            if (userRepository.existsByNationalId(request.nationalId())) {
                throw new ZarnabException(ExceptionType.NATIONAL_ID_ALREADY_EXISTS);
            }
        }

        if (nationalIdChanged || mobileChanged) {
            if (Boolean.FALSE.equals(shahkarClient.verifyMobileOwner(request.nationalId(), request.mobileNumber()).block())) {
                throw new ZarnabException(ExceptionType.INVALID_MOBILE_NATIONAL_SHAHKAR);
            }
        }

        user.setEnabled(request.enabled());
        user.setRoles(request.roles());
        user.getNaturalPersonProfile().setNationalId(request.nationalId());
        user.setMobileNumber(request.mobileNumber());
        user.getNaturalPersonProfile().setFirstName(request.firstName());
        user.getNaturalPersonProfile().setLastName(request.lastName());
        user.getNaturalPersonProfile().setNationalId(request.nationalId());
        user.setPostalCode(request.postalCode());
        user.setAddress(request.address());

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

    @Override
    public LoginResult switchUserRole(User user, String newRole) throws AccessDeniedException {
        boolean hasRole = user.getRoles().stream()
                .anyMatch(r -> r.name().equalsIgnoreCase(newRole));

        Role role;
        try {
            role = Role.valueOf(newRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AccessDeniedException("Invalid input role");
        }

        if (!hasRole) {
            throw new AccessDeniedException("You do not have permission for this role");
        }

        return createLoginResult(user, role);
    }


    private LoginResult createLoginResult(User user, Role activeRole) {
        String accessToken = jwtService.generateAccessToken(user, activeRole);
        String refreshToken = jwtService.generateRefreshToken(user, activeRole);
        user.setActiveRole(activeRole);
        return new LoginResult(accessToken, refreshToken, user);
    }

    private LoginResult createLoginResult(User user) {
        Role defaultRole = getDefaultRole(user);
        return createLoginResult(user, defaultRole);
    }

    private Role getDefaultRole(User user) {
        Role role = Role.CUSTOMER;
        if (RoleUtil.hasRole(user, Role.ADMIN)) {
            role = Role.ADMIN;
        } else if (RoleUtil.hasRole(user, Role.COUNTER)) {
            role = Role.COUNTER;
        } else if (RoleUtil.hasRole(user, Role.REPRESENTATIVE)) {
            role = Role.REPRESENTATIVE;
        } else {
            RoleUtil.hasRole(user, Role.CUSTOMER);
        }
        return role;
    }

    private String createRegistrationToken(String mobileNumber) {
        String regToken = UUID.randomUUID().toString();
        registrationTokenStore.store(REG_TOKEN_KEY_PREFIX + regToken, mobileNumber, regTokenExpiration, TimeUnit.MILLISECONDS);
        return regToken;
    }
}
