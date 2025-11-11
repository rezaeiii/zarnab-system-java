package com.zarnab.panel.auth.controller;

import com.zarnab.panel.auth.dto.UserManagementDtos;
import com.zarnab.panel.auth.service.AuthService;
import com.zarnab.panel.clients.dto.FlatPersonInquiryResponse;
import com.zarnab.panel.clients.dto.PersonInquiryResponse;
import com.zarnab.panel.core.annotations.PageableParam;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final AuthService authService;
    private final ProfileService profileService;

    @Operation(summary = "List users with pagination, filtering, and sorting")
    @GetMapping
    public ResponseEntity<PageableResponse<UserManagementDtos.UserResponse>> listUsers(
            @PageableParam @ParameterObject PageableRequest pageableRequest) {
        return ResponseEntity.ok(authService.listUsers(pageableRequest));
    }

    @PostMapping
    public ResponseEntity<UserManagementDtos.UserResponse> createUser(@Valid @RequestBody UserManagementDtos.CreateUserRequest request) {
        return ResponseEntity.ok(authService.createUser(request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserManagementDtos.UserResponse> loadUser(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.loadUser(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserManagementDtos.UserResponse> updateUser(@PathVariable Long userId, @Valid @RequestBody UserManagementDtos.UpdateUserRequest request) {
        return ResponseEntity.ok(authService.updateUser(userId, request));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        authService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get person info by national ID and Jalali birthdate")
    @PostMapping("/inquiry")
    public FlatPersonInquiryResponse getPersonInfo(@Valid @RequestBody UserManagementDtos.PersonInquiryRequest request) {
        return profileService.getPersonInfo(request.nationalId(), request.mobileNumber(), request.jalaliBirthDate());
    }
}
