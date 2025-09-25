package com.zarnab.panel.ingot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TransferStatus {
    PENDING("در انتظار"),
    VERIFIED("تایید شده"),
    COMPLETED("تکمیل شده"),
    CANCELED("لغو شده");

    private final String persianDescription;

    public String getName() {
        return name();
    }
}
