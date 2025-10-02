//package com.zarnab.panel.core.filter;
//
//import com.zarnab.panel.common.util.CharacterUtil;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletRequestWrapper;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Component
//public class PersianDigitNormalizationFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
//            @Override
//            public String getParameter(String name) {
//                return CharacterUtil.normalizeDigits(super.getParameter(name));
//            }
//
//            @Override
//            public String[] getParameterValues(String name) {
//                String[] values = super.getParameterValues(name);
//                if (values == null) return null;
//                return Stream.of(values)
//                        .map(CharacterUtil::normalizeDigits)
//                        .toArray(String[]::new);
//            }
//
//            @Override
//            public Map<String, String[]> getParameterMap() {
//                Map<String, String[]> original = super.getParameterMap();
//                return original.entrySet().stream()
//                        .collect(Collectors.toMap(
//                                Map.Entry::getKey,
//                                e -> Stream.of(e.getValue())
//                                        .map(CharacterUtil::normalizeDigits)
//                                        .toArray(String[]::new)
//                        ));
//            }
//
//            @Override
//            public String getHeader(String name) {
//                return CharacterUtil.normalizeDigits(super.getHeader(name));
//            }
//        };
//
//        filterChain.doFilter(wrapper, response);
//    }
//}
