package com.zarnab.panel.clients.dto.faraboom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FaraboomTransferResponse {

    // This is the reference number returned by the server
    @JsonProperty("reference_number")
    private String serverReferenceNumber;

    @JsonProperty("tracking_code")
    private String trackingCode;

    @JsonProperty("instant_debit")
    private boolean instantDebit;

    // These fields are often present in success/error responses
    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("operation_time")
    private long operationTime;

    @JsonProperty("ref_id")
    private String refId;
}