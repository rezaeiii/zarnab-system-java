package com.zarnab.panel.auth.dto.req;

import jakarta.validation.constraints.NotBlank;

public record ToggleRoleRequest(
    @NotBlank String path,
    @NotBlank String method,
    @NotBlank String roleName
) {}
