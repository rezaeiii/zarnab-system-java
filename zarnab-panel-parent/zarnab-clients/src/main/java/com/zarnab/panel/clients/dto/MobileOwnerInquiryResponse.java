package com.zarnab.panel.clients.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MobileOwnerInquiryResponse {

    private ResponseContext responseContext;

    @JsonProperty("isMatched")
    private boolean matched;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseContext {
        private Status status; // This should be an object, not individual fields
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Status {
        private int code;
        private String message;
    }
}