package com.zarnab.panel.ingot.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class SetReceiverRequest {
    @NotBlank
    private String batchId;
    @NotBlank
    private String receiverMobileNumber;
}
