package com.zarnab.panel.inheritance.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum HeirDocumentStatus {
    PENDING("در انتظار بارگذاری"),
    UPLOADED("بارگذاری شده"),
    REJECTED("رد شده"),
    APPROVED("تایید شده");

    private final String friendlyName;

    public String getName() {
        return this.name();
    }
}
