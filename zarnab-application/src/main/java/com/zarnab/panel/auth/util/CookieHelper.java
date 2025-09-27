package com.zarnab.panel.auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * A helper component to encapsulate the logic for creating secure, HttpOnly cookies.
 * Cookie properties are configurable via application.properties to support different environments.
 */
@Component
public class CookieHelper {

    @Value("${zarnab.security.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpiration;

    @Value("${spring.profiles.active:prod}")
    private String[] activeProfiles;

    /**
     * Creates a secure, HttpOnly cookie for the refresh token and returns it as an HTTP header.
     *
     * @param refreshToken The JWT refresh token.
     * @return The HttpHeaders object containing the Set-Cookie header.
     */
    public HttpHeaders createRefreshTokenCookie(String refreshToken) {
        List<String> devs = Arrays.asList("dev", "docker-dev");
        boolean isDevProfile = Arrays.stream(activeProfiles).anyMatch(devs::contains);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(!isDevProfile) // Configurable for dev (http) vs prod (https)
                .path("/")
                .maxAge(refreshTokenExpiration / 1000)
                .sameSite(isDevProfile ? "Lax" : "Strict") // Lax for dev, Strict for prod
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        return headers;
    }

    /**
     * Creates an HTTP header to clear the refresh token cookie.
     *
     * @return The HttpHeaders object containing the Set-Cookie header to expire the cookie.
     */
    public HttpHeaders clearRefreshTokenCookie() {
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(!Arrays.stream(activeProfiles).anyMatch(p -> p.equals("dev") || p.equals("docker-dev")))
                .path("/")
                .maxAge(0)
                .sameSite(Arrays.stream(activeProfiles).anyMatch(p -> p.equals("dev") || p.equals("docker-dev")) ? "Lax" : "Strict")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        return headers;
    }
}