package com.zarnab.panel.core.exception;

import com.zarnab.panel.common.exception.IZarnabException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


/**
 * A centralized and comprehensive catalog of all application errors.
 * This enum is the Single Source of Truth for error definitions.
 * <p>
 * Naming Convention for messageKey: "error.{domain}.{context}"
 */
@Getter
@RequiredArgsConstructor
public enum ExceptionType implements IZarnabException {

    // --- Business Logic Errors (from your services) ---
    INVALID_OTP(HttpStatus.UNAUTHORIZED, 1000, "error.auth.invalidOtp"),
    EXPIRE_OTP(HttpStatus.UNAUTHORIZED, 1001, "error.auth.expireOtp"),
    TOKEN_EXPIRED_OR_INVALID(HttpStatus.UNAUTHORIZED, 1002, "error.auth.token.invalid"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, 1003, "error.user.alreadyExists"),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, 2002, "error.auth.otp.cooldown"),
    NEW_MOBILE_NUMBER_EQUAL_OLD(HttpStatus.BAD_REQUEST, 2002, "error.user.newMobileEqualOld"),
    INGOT_NOT_FOUND(HttpStatus.NOT_FOUND, 3001, "error.ingot.notFound"),
    INGOT_ALREADY_EXISTS(HttpStatus.CONFLICT, 3002, "error.ingot.alreadyExists"),
    INGOT_OWNERSHIP_ERROR(HttpStatus.FORBIDDEN, 3002, "error.ingot.ownership"),
    INGOT_ALREADY_OWNERSHIP(HttpStatus.CONFLICT, 3002, "error.ingot.alreadyOwnership"),
    INGOT_IS_STOLEN(HttpStatus.FORBIDDEN, 3003, "error.ingot.isStolen"),
    TRANSFER_NOT_FOUND(HttpStatus.NOT_FOUND, 3004, "error.transfer.notFound"),
    TRANSFER_SELLER_MISMATCH(HttpStatus.FORBIDDEN, 3005, "error.transfer.sellerMismatch"),
    TRANSFER_INVALID_STATUS(HttpStatus.CONFLICT, 3006, "error.transfer.invalidStatus"),
    TRANSFER_PERMISSION_DENIED(HttpStatus.FORBIDDEN, 3007, "error.transfer.permissionDenied"),
    DUPLICATE_THEFT_REPORT(HttpStatus.CONFLICT, 3008, "error.ingot.duplicateTheftReport"),
    THEFT_REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, 3009, "error.ingot.theftReportNotFound"),
    INVALID_TRANSFER_BUYER(HttpStatus.BAD_REQUEST, 3009, "error.transfer.invalidBuyer"),

    // --- Spring Framework & Web Layer Errors ---
    VALIDATION_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, 4001, "error.validation.failed"),
    MALFORMED_JSON(HttpStatus.BAD_REQUEST, 4002, "error.json.malformed"),
    MISSING_REQUEST_PARAM(HttpStatus.BAD_REQUEST, 4003, "error.request.missingParam"),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, 4004, "error.request.typeMismatch"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 4005, "error.request.methodNotAllowed"),
    MEDIA_TYPE_NOT_SUPPORTED(HttpStatus.UNSUPPORTED_MEDIA_TYPE, 4006, "error.request.mediaTypeNotSupported"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, 4007, "error.request.notFound"),
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, 4008, "error.request.payloadTooLarge"),

    // --- Generic & Fallback Errors ---
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 9001, "error.server.internal"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 9002, "error.user.notFound"),
    INVALID_MOBILE_NATIONAL_SHAHKAR(HttpStatus.BAD_REQUEST, 9003, "error.user.invalidMatchNationalMobile");

    private final HttpStatus status;
    private final int code;
    private final String messageKey;
}
