package com.zarnab.panel.ingot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TransferStatus {
    PENDING_SELLER_VERIFICATION("در انتظار تایید فروشنده"),
    COMPLETED("تکمیل شده"),
    CANCELED("لغو شده"),
    EXPIRED("منقضی شده");

    private final String persianDescription;

    public String getName() {
        return name();
    }
}
