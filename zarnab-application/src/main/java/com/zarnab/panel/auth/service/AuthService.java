package com.zarnab.panel.auth.service;

import com.zarnab.panel.auth.dto.LoginResult;
import com.zarnab.panel.auth.dto.VerifyOtpResult;
import com.zarnab.panel.auth.dto.req.InitiateLoginRequest;
import com.zarnab.panel.auth.dto.req.RegisterRequest;

/**
 * Main application service for handling the user authentication and registration business logic.
 * This service acts as an orchestrator, coordinating various components to fulfill the use cases.
 */
public interface AuthService {

    void initiateLogin(InitiateLoginRequest request);

    VerifyOtpResult verifyOtp(String mobileNumber, String otp);

    LoginResult registerUser(RegisterRequest request);
}

