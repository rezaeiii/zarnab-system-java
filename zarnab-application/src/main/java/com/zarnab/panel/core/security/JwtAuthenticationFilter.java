package com.zarnab.panel.core.security;

import com.zarnab.panel.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * A custom Spring Security filter that runs once per request.
 * This filter is responsible for intercepting API requests, validating the JWT access token
 * from the Authorization header, and setting the user's authentication state in the
 * SecurityContextHolder if the token is valid.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // If the header is missing or doesn't start with "Bearer ", proceed to the next filter.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token from the header (e.g., "Bearer eyJhbGciOiJIUzI1NiJ9...")
        final String jwt = authHeader.substring(7);
        final String mobileNumber = jwtService.extractMobileNumber(jwt);

        // If a mobile number is extracted and the user is not already authenticated
        if (mobileNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load the user from the database via our port
            userRepository.findByMobileNumber(mobileNumber).ifPresent(user -> {
                // If the user is found, validate the token
                if (jwtService.isTokenValid(jwt, user)) {
                    // Create an authentication token and set it in the security context
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, // Using our domain User object as the principal
                            null,
                            user.getRoles().stream()
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                                    .collect(Collectors.toSet())
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            });
        }
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
