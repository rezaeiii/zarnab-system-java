package com.zarnab.panel.profile.service;

import com.zarnab.panel.auth.model.NaturalPersonProfileEmbeddable;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.auth.service.otp.OtpPurpose;
import com.zarnab.panel.auth.service.otp.OtpService;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.common.file.service.FileStorageService;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.profile.dto.ProfileDtos;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final FileStorageService fileStorageService;

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

    @Override
    @Transactional
    public void updateProfile(ProfileDtos.UpdateProfileRequest request, MultipartFile image, User user) {
        NaturalPersonProfileEmbeddable profile = user.getNaturalPersonProfile();
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());

        String oldImage = profile.getNationalCardImageUrl();
        if (image != null && !image.isEmpty()) {
            ObjectWriteResponse imageObject = fileStorageService.uploadFile(image);
            profile.setNationalCardImageUrl(imageObject.object());
            if (oldImage != null && !oldImage.isEmpty())
                fileStorageService.removeFile(oldImage);
        }

        user.setAddress(request.address());
        user.setPostalCode(request.postalCode());
        userRepository.save(user);
    }
}
