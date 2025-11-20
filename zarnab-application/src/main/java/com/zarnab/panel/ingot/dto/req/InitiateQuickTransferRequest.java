package com.zarnab.panel.ingot.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class InitiateQuickTransferRequest {

    @NotBlank(message = "Sender mobile number is required.")
    @Pattern(regexp = "^09\\d{9}$", message = "Invalid mobile number format.")
    private String senderMobileNumber;

    @NotBlank(message = "Ingot serial number is required.")
    private String ingotSerialNumber;
}
