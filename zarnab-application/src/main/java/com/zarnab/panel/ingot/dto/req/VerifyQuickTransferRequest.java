package com.zarnab.panel.ingot.dto.req;

import lombok.Data;

@Data
public class VerifyQuickTransferRequest {
    private String batchId;
    private String senderVerificationCode;
    private String receiverVerificationCode;
}
