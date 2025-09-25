package com.zarnab.panel.ingot.dto.req;

import lombok.Data;

@Data
public class VerifyTransferRequest {
    private Long transferId;
    private String verificationCode;
}
