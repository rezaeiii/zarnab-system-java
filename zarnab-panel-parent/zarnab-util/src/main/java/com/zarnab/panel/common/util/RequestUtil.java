//package com.zarnab.panel.common.util;
//
//import com.quartz.common.exception.LoginRequiredException;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import static com.quartz.common.constants.ConfigConstants.USER_HEADER_KEY;
//
//public class RequestUtil {
//
//    public static HttpServletRequest getCurrentRequest() {
//        ServletRequestAttributes attributes =
//                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//
//        HttpServletRequest httpServletRequest = (attributes != null) ? attributes.getRequest() : null;
//        if (httpServletRequest == null) {
//            throw new IllegalArgumentException("request is null");
//        }
//        return httpServletRequest;
//    }
//
//    public static Long getCurrentUserId() {
//        String header = getCurrentRequest().getHeader(USER_HEADER_KEY);
//        requireLogin(header);
//        return Long.parseLong(header);
//    }
//
//    public static void requireLogin() {
//        String header = getCurrentRequest().getHeader(USER_HEADER_KEY);
//        requireLogin(header);
//    }
//
//    public static void requireLogin(String header) {
//        if (header == null || header.isEmpty())
//            throw new LoginRequiredException();
//    }
//}
