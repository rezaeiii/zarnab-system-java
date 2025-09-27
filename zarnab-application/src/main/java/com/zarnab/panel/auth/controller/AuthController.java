package com.zarnab.panel.auth.controller;

import com.zarnab.panel.auth.dto.LoginResult;
import com.zarnab.panel.auth.dto.VerifyOtpResult;
import com.zarnab.panel.auth.dto.req.InitiateLoginRequest;
import com.zarnab.panel.auth.dto.req.RegisterRequest;
import com.zarnab.panel.auth.dto.req.VerifyOtpRequest;
import com.zarnab.panel.auth.dto.res.LoginResponse;
import com.zarnab.panel.auth.dto.res.VerifyOtpResponse;
import com.zarnab.panel.auth.service.AuthService;
import com.zarnab.panel.auth.service.otp.OtpPurpose;
import com.zarnab.panel.auth.service.otp.OtpService;
import com.zarnab.panel.auth.util.CookieHelper;
import com.zarnab.panel.common.annotation.fileValidator.FileConstraint;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.zarnab.panel.auth.dto.res.MeResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.zarnab.panel.auth.model.User;

/**
 * Web layer entry point for user authentication and registration flows.
 * This controller is responsible for handling HTTP requests, delegating to the AuthService,
 * and mapping service-layer results to HTTP responses.
 */
@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;
    private final CookieHelper cookieHelper;

    /**
     * Step 1: Initiates the login/registration process by sending an OTP.
     */
    @PostMapping("/initiate")
    public ResponseEntity<Void> initiateLogin(@Valid @RequestBody InitiateLoginRequest request) {
        authService.initiateLogin(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Step 2: Verifies the OTP and directs the user to either login or register.
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        VerifyOtpResult serviceResult = authService.verifyOtp(request.mobileNumber(), request.otp());

        if (serviceResult.status() == VerifyOtpResult.Status.LOGIN_SUCCESS) {
            return buildLoginSuccessResponse(serviceResult.loginResult());
        } else {
            // If registration is required, just send back the registration token.
            VerifyOtpResponse responseDto = new VerifyOtpResponse(
                    VerifyOtpResponse.Status.REGISTRATION_REQUIRED,
                    serviceResult.registrationToken(),
                    null // No login response
            );
            return ResponseEntity.ok(responseDto);
        }
    }

    /**
     * Step 3: Registers a new user and logs them in.
     */
    @PostMapping(value = "/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<VerifyOtpResponse> register(
            @FileConstraint(maxFiles = 1) @RequestPart(name = "nationalIdImage", required = false) MultipartFile nationalIdImage,
//            @Valid @RequestPart RegisterRequest request
            @RequestParam("nationalId") String nationalId,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("registrationToken") String registrationToken
    ) {
        RegisterRequest request = new RegisterRequest(registrationToken, firstName, lastName, nationalId);
        LoginResult loginResult = authService.registerUser(request, nationalIdImage);
        return buildLoginSuccessResponse(loginResult);
    }

    /**
     * Returns authenticated user's profile based on the current security context.
     */
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(MeResponse.from(user));
    }

    /**
     * Refresh access and refresh tokens using HttpOnly refresh token cookie.
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        LoginResult loginResult = authService.refreshTokens(refreshToken);
        HttpHeaders cookieHeader = cookieHelper.createRefreshTokenCookie(loginResult.refreshToken());
        return ResponseEntity.ok()
                .headers(cookieHeader)
                .body(new LoginResponse(loginResult.accessToken()));
    }

    /**
     * Logs the user out by clearing the refresh token cookie.
     * The client is responsible for deleting the access token.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User user) {
        if (user != null) {
            otpService.clearOtp(OtpPurpose.AUTH, user.getMobileNumber());
        }
        HttpHeaders cookieHeader = cookieHelper.clearRefreshTokenCookie();
        return ResponseEntity.ok()
                .headers(cookieHeader)
                .build();
    }

    /**
     * A unified helper method to build the final HTTP response for any successful authentication.
     * It creates the web DTO and sets the refresh token in a secure, configurable HttpOnly cookie.
     *
     * @param loginResult The result from the service layer containing tokens.
     * @return A ResponseEntity with the access token in the body and the refresh token in a cookie.
     */
    private ResponseEntity<VerifyOtpResponse> buildLoginSuccessResponse(LoginResult loginResult) {
        // Use the helper to create the cookie header
        HttpHeaders cookieHeader = cookieHelper.createRefreshTokenCookie(loginResult.refreshToken());

        VerifyOtpResponse responseDto = new VerifyOtpResponse(
                VerifyOtpResponse.Status.LOGIN_SUCCESS,
                null, // No registration token
                new LoginResponse(loginResult.accessToken())
        );

        return ResponseEntity.ok()
                .headers(cookieHeader) // Set the cookie header
                .body(responseDto);
    }
}