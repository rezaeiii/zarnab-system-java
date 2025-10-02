package com.zarnab.panel.profile.controller;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.profile.dto.ProfileDtos;
import com.zarnab.panel.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
