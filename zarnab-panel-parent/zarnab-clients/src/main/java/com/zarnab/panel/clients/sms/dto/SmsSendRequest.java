package com.zarnab.panel.clients.sms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record SmsSendRequest(
    @JsonProperty("lineNumber") Long lineNumber,
    @JsonProperty("messageTexts") List<String> messageTexts,
    @JsonProperty("mobiles") List<String> mobiles,
    @JsonProperty("senddatetime") String senddatetime
) {}
