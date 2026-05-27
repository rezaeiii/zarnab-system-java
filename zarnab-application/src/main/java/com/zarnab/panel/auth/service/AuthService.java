package com.zarnab.panel.auth.service;

import com.zarnab.panel.auth.dto.LoginResult;
import com.zarnab.panel.auth.dto.UserManagementDtos;
import com.zarnab.panel.auth.dto.VerifyOtpResult;
import com.zarnab.panel.auth.dto.req.InitiateLoginRequest;
import com.zarnab.panel.auth.dto.req.RegisterRequest;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;

/**
 * Main application service for handling the user authentication and registration business logic.
 * This service acts as an orchestrator, coordinating various components to fulfill the use cases.
 */
public interface AuthService {

    void initiateLogin(InitiateLoginRequest request);

    VerifyOtpResult verifyOtp(String mobileNumber, String otp);

    LoginResult registerUser(RegisterRequest request, MultipartFile nationalIdImage);

    LoginResult refreshTokens(String refreshToken);

    PageableResponse<UserManagementDtos.UserResponse> listUsers(PageableRequest pageableRequest);

    UserManagementDtos.UserResponse createUser(UserManagementDtos.CreateUserRequest request);

    UserManagementDtos.UserResponse loadUser(Long userId);

    User loadUserProfile(Long userId);

    UserManagementDtos.UserResponse updateUser(Long userId, UserManagementDtos.UpdateUserRequest request);

    void deleteUser(Long userId);

    LoginResult switchUserRole(User user, String newRole) throws AccessDeniedException;
}
