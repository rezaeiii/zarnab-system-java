package com.zarnab.panel.core.util;

import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;

import java.util.stream.Stream;

public class RoleUtil {

    public static boolean hasRole(User user, Role... roles) {
        return user.getRoles().stream()
                .anyMatch(r -> Stream.of(roles).anyMatch(rr -> r.name().equals(rr.name())));
    }
}
