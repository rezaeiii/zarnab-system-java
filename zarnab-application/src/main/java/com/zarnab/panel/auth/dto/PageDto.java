package com.zarnab.panel.auth.dto;

import java.util.List;

public record PageDto(String path, String method, List<String> roles) {
}
