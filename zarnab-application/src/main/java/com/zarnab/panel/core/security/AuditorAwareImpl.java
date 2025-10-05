package com.zarnab.panel.core.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

    @NotNull
    @Override
    public Optional<String> getCurrentAuditor() {
        if (SecurityContextHolder.getContext().getAuthentication() != null)
            return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());
        return Optional.empty();
    }
}
