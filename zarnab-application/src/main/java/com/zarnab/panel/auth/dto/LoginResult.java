package com.zarnab.panel.auth.dto;

import com.zarnab.panel.auth.model.User;

/**
 * Represents the outcome of a successful login or registration.
 * Contains all necessary data for the web layer to build a response.
 */
public record LoginResult(String accessToken, String refreshToken, User user) {
}
