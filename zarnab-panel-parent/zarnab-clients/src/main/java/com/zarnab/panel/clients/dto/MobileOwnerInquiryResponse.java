package com.zarnab.panel.clients.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MobileOwnerInquiryResponse {
    private boolean isMatched;
    private int code;
    private String message;
}