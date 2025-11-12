package com.zarnab.panel.profile.service;

import com.zarnab.panel.auth.model.NaturalPersonProfileEmbeddable;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.auth.service.otp.OtpPurpose;
import com.zarnab.panel.auth.service.otp.OtpService;
import com.zarnab.panel.clients.dto.FlatPersonInquiryResponse;
import com.zarnab.panel.clients.service.PersonInquiryClient;
import com.zarnab.panel.clients.service.ShahkarInquiryClient;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.common.file.service.FileStorageService;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.profile.dto.ProfileDtos;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final FileStorageService fileStorageService;
    private final ShahkarInquiryClient shahkarInquiryClient;
    private final PersonInquiryClient personInquiryClient;
    private final RedisTemplate<String, FlatPersonInquiryResponse> redisTemplate;

    private static final String PERSON_INFO_CACHE_KEY_PREFIX = "person_info:";
    private static final Duration PERSON_INFO_CACHE_TTL = Duration.ofHours(3);

    @Override
    public void initiateChangeMobile(ProfileDtos.InitiateChangeMobileRequest request, User user) {
        if (userRepository.existsByMobileNumber(request.newMobileNumber())) {
            throw new ZarnabException(ExceptionType.USER_ALREADY_EXISTS);
        }

        if (user.getMobileNumber().equals(request.newMobileNumber())) {
            throw new ZarnabException(ExceptionType.TOO_MANY_REQUESTS);
        }

        if (Boolean.FALSE.equals(shahkarInquiryClient.verifyMobileOwner(user.getNaturalPersonProfile().getNationalId(), request.newMobileNumber()).block())) {
            throw new ZarnabException(ExceptionType.INVALID_MOBILE_NATIONAL_SHAHKAR);
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
        profile.setBirthDate(request.birthDate());

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

    @Override
    public FlatPersonInquiryResponse getPersonInfo(String nationalId, String mobileNumber, String jalaliBirthDate) {

        if (Boolean.FALSE.equals(shahkarInquiryClient.verifyMobileOwner(nationalId, mobileNumber).block())) {
            throw new ZarnabException(ExceptionType.INVALID_MOBILE_NATIONAL_SHAHKAR);
        }

        String cacheKey = PERSON_INFO_CACHE_KEY_PREFIX + nationalId + ":" + jalaliBirthDate;
        FlatPersonInquiryResponse cachedInfo = redisTemplate.opsForValue().get(cacheKey);

        if (cachedInfo != null) {
            return cachedInfo;
        }

        FlatPersonInquiryResponse personInfo = personInquiryClient.getPersonInfo(nationalId, jalaliBirthDate).block();
        if (personInfo != null) {
            redisTemplate.opsForValue().set(cacheKey, personInfo, PERSON_INFO_CACHE_TTL);
        }
        return personInfo;
    }
}
