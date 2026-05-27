package com.zarnab.panel.core.security;

import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service responsible for JWT-related operations for AUTHENTICATION:
 * generation, validation, and claim extraction for access and refresh tokens.
 */
@Service
public class JwtService {

    @Value("${zarnab.security.jwt.secret-key}")
    private String secretKey;

    @Value("${zarnab.security.jwt.access-token-expiration-ms}")
    private long accessTokenExpiration;

    @Value("${zarnab.security.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpiration;

    @Value("${zarnab.security.jwt.registration-token-expiration-ms}")
    private long registrationTokenExpiration;

    // --- Public Methods for Access/Refresh Tokens ---

    public String extractMobileNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractActiveRole(String token) {
        return extractClaim(token, claims -> claims.get("activeRole", String.class));
    }

    public boolean isTokenValid(String token, User user) {
        final String mobileNumber = extractMobileNumber(token);
        return (mobileNumber.equals(user.getMobileNumber())) && !isTokenExpired(token);
    }

    public String generateAccessToken(User user, Role activeRole) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream().map(Enum::name).collect(Collectors.toList()));
        claims.put("activeRole", activeRole);
        return buildToken(claims, user.getMobileNumber(), accessTokenExpiration);
    }

    public String generateRefreshToken(User user, Role activeRole) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("activeRole", activeRole.name());
        return buildToken(extraClaims, user.getMobileNumber(), refreshTokenExpiration);
    }


    public String generateRegistrationToken(String mobileNumber) {
        return buildToken(new HashMap<>(), mobileNumber, registrationTokenExpiration);
    }


    // --- Private Helper Methods ---
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = this.secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
