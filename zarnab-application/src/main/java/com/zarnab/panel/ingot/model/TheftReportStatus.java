package com.zarnab.panel.ingot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TheftReportStatus {
    PENDING("در انتظار بررسی"),
    APPROVED("تایید شده"),
    REJECTED("رد شده");

    private final String persianDescription;

    public String getName() {
        return name();
    }
}
