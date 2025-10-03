package com.zarnab.panel.clients.sms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SmsSendData(
        @JsonProperty("packId") String packId,
        @JsonProperty("messageIds") List<Long> messageIds
) {
}
