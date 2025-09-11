package com.zarnab.panel.common.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {

    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = (attributes != null) ? attributes.getRequest() : null;
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        return request;
    }

}
