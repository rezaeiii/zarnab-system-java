package com.zarnab.panel.auth.dto.res;

import lombok.Builder;

@Builder
public record LoginResponse(String accessToken) {
}