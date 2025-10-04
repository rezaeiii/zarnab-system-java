package com.zarnab.panel.profile.controller;

import com.zarnab.panel.auth.dto.LoginResult;
import com.zarnab.panel.auth.dto.req.RegisterRequest;
import com.zarnab.panel.auth.dto.res.VerifyOtpResponse;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.common.annotation.fileValidator.FileConstraint;
import com.zarnab.panel.profile.dto.ProfileDtos;
import com.zarnab.panel.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/change-mobile/initiate")
    public ResponseEntity<Void> initiateChangeMobile(@RequestBody ProfileDtos.InitiateChangeMobileRequest request, @AuthenticationPrincipal User user) {
        profileService.initiateChangeMobile(request, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-mobile/verify")
    public ResponseEntity<Void> verifyChangeMobile(@RequestBody ProfileDtos.VerifyChangeMobileRequest request, @AuthenticationPrincipal User user) {
        profileService.verifyChangeMobile(request, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> updateProfile(@RequestParam("firstName") String firstName,
                                              @RequestParam("lastName") String lastName,
                                              @FileConstraint(maxFiles = 1) @RequestPart(name = "nationalIdImage", required = false) MultipartFile nationalIdImage,
                                              @AuthenticationPrincipal User user) {
        ProfileDtos.UpdateProfileRequest req = new ProfileDtos.UpdateProfileRequest(firstName, lastName);
        profileService.updateProfile(req, nationalIdImage, user);
        return ResponseEntity.ok().build();
    }
}
