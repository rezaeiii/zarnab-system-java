package com.zarnab.panel.clients.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddressInquiryResponse(
        String postalCode,
        String address,
        ResponseContext responseContext
) {
    public record ResponseContext(
            Status status,
            String requestId,
            String correlationId,
            String navigationURI,
            String nextStepToken,
            String userSessionId,
            Object custom
    ) {
        public record Status(
                int code,
                String message,
                Object[] details
        ) {}
    }
}
