package com.zarnab.panel.inheritance.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the status of an inheritance case throughout its lifecycle.
 */
@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum InheritanceStatus {
    /**
     * The case has been initiated by a claimant, and a tracking code has been generated.
     * The deceased's account is temporarily locked.
     */
    INITIATED("در انتظار بارگذاری مدارک اولیه"),

    /**
     * The primary claimant has uploaded the initial documents (death and inheritance certificates).
     * The case is now waiting for an administrator to review these documents.
     */
    PENDING_ADMIN_REVIEW("در انتظار بررسی ادمین"),

    /**
     * The administrator has verified the initial documents and listed the heirs.
     * The system is now waiting for the other heirs to upload their identity documents.
     */
    PENDING_HEIR_DOCUMENTS("در انتظار تکمیل مدارک ورثه"),

    /**
     * All heirs have uploaded their documents.
     * The case is now ready for final review and approval by the administrator.
     */
    PENDING_FINAL_APPROVAL("در انتظار تایید نهایی ادمین"),

    /**
     * The administrator has approved the case. The assets are now ready for transfer.
     */
    COMPLETED("تکمیل شده"),

    /**
     * The administrator has rejected the case at some point in the process.
     */
    REJECTED("رد شده");

    private final String friendlyName;

    // This getter is needed for Jackson to serialize the enum's standard name
    public String getName() {
        return this.name();
    }
}
