package com.zarnab.panel.core.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * A custom Authentication object to hold credentials for OTP-based login.
 * This object carries the mobile number and the OTP code through the security filter chain.
 */
public class OtpAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal; // Typically the mobile number
    private final Object credentials; // The OTP

    /**
     * Constructor for an unauthenticated token.
     */
    public OtpAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    /**
     * Constructor for a fully authenticated token.
     */
    public OtpAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
