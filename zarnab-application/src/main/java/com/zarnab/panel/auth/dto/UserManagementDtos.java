package com.zarnab.panel.auth.dto;

import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class UserManagementDtos {

    public record UserResponse(
            Long id,
            String mobileNumber,
            String firstName,
            String lastName,
            String nationalId,
            boolean enabled,
            Set<Role> roles
    ) {
        public static UserResponse from(User user) {
            return new UserResponse(
                    user.getId(),
                    user.getMobileNumber(),
                    user.getNaturalPersonProfile() != null ? user.getNaturalPersonProfile().getFirstName() : null,
                    user.getNaturalPersonProfile() != null ? user.getNaturalPersonProfile().getLastName() : null,
                    user.getNaturalPersonProfile() != null ? user.getNaturalPersonProfile().getNationalId() : null,
                    user.isEnabled(),
                    user.getRoles()
            );
        }
    }

    public record CreateUserRequest(
            @NotBlank
            @Pattern(regexp = "^09[0-9]{9}$", message = "Invalid mobile number format")
            String mobileNumber,

            @NotBlank
            String firstName,

            @NotBlank
            String lastName,

            @NotBlank
            @Pattern(regexp = "^[0-9]{10}$", message = "National ID must be 10 digits")
            String nationalId,

            @Size(min = 1)
            Set<Role> roles
    ) {
    }

    public record UpdateUserRequest(
            @NotBlank
            String firstName,

            @NotBlank
            String lastName,

            @NotBlank
            @Pattern(regexp = "^[0-9]{10}$", message = "National ID must be 10 digits")
            String nationalId,

            boolean enabled,

            @Size(min = 1)
            Set<Role> roles
    ) {
    }
}
