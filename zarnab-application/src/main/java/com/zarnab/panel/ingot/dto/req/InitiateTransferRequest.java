package com.zarnab.panel.ingot.dto.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class InitiateTransferRequest {
    @NotNull
    private Long ingotId;

    @Pattern(regexp = "^09[0-9]{9}$", message = "Invalid mobile number format")
    private String buyerMobileNumber;

    private Boolean toZarnab = false;
}
