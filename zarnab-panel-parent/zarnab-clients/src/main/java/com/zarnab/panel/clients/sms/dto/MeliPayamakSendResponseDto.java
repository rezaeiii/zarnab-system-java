package com.zarnab.panel.clients.sms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeliPayamakSendResponseDto {
    @JsonProperty("Value")
    private String value;

    @JsonProperty("RetStatus")
    private int retStatus;

    @JsonProperty("StrRetStatus")
    private String strRetStatus;
}
