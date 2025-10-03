package com.zarnab.panel.clients.sms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SmsSendResponse(
        @JsonProperty("data") SmsSendData data,
        @JsonProperty("status") String status,
        @JsonProperty("message") String message
) {
}
