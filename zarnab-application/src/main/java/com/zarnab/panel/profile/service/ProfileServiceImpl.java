package com.zarnab.panel.profile.service;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.auth.service.otp.OtpPurpose;
import com.zarnab.panel.auth.service.otp.OtpService;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.profile.dto.ProfileDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final OtpService otpService;

    @Override
    public void initiateChangeMobile(ProfileDtos.InitiateChangeMobileRequest request, User user) {
        if (userRepository.existsByMobileNumber(request.newMobileNumber())) {
            throw new ZarnabException(ExceptionType.USER_ALREADY_EXISTS);
        }

        otpService.sendOtp(OtpPurpose.CHANGE_MOBILE, request.newMobileNumber());
    }

    @Override
    @Transactional
    public void verifyChangeMobile(ProfileDtos.VerifyChangeMobileRequest request, User user) {
        otpService.verifyOtp(OtpPurpose.CHANGE_MOBILE, request.newMobileNumber(), request.otp());

        user.setMobileNumber(request.newMobileNumber());
        userRepository.save(user);
    }
}
