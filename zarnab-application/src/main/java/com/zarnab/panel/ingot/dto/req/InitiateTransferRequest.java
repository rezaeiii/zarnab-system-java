package com.zarnab.panel.ingot.dto.req;

import lombok.Data;

@Data
public class InitiateTransferRequest {
    private Long ingotId;
    private String buyerMobileNumber;
}
