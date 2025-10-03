package com.zarnab.panel.auth.controller;

import com.zarnab.panel.auth.dto.UserManagementDtos;
import com.zarnab.panel.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<UserManagementDtos.UserResponse>> listUsers() {
        return ResponseEntity.ok(authService.listUsers());
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
}
