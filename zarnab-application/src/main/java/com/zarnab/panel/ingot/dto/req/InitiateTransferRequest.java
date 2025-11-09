package com.zarnab.panel.ingot.dto.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class InitiateTransferRequest {
    @NotEmpty
    private List<Long> ingotIds;

    @Pattern(
            regexp = "^$|^09\\d{9}$",
            message = "Invalid mobile number format"
    )
    private String buyerMobileNumber;

    @NotNull
    private TransferTarget to;

    public enum TransferTarget {
        COUNTER,
        ZARNAB,
        CUSTOMER
    }
}
