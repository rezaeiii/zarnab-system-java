package com.zarnab.panel.auth.dto;

import com.zarnab.panel.auth.model.User;

import java.util.List;

public record SwitchRoleResult(String accessToken, String refreshToken, User user, List<PageDto> pages) {
}
