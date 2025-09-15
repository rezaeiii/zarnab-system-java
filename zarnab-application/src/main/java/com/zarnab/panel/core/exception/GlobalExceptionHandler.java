package com.zarnab.panel.core.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.zarnab.panel.common.exception.IZarnabException;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.common.translate.Translator;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Tracer tracer;
    private static final String ERROR_DOCS_BASE_URI = "https://zarnab.panel/errors/";

    /**
     * Central handler for all custom, predictable business exceptions.
     */
    @ExceptionHandler(ZarnabException.class)
    public ResponseEntity<ProblemDetailDto> handleZarnabException(ZarnabException ex, HttpServletRequest request) {
        String detail = Translator.translate(ex.getExceptionType().getMessageKey(), ex.getArgs());
        return buildProblemDetail(ex.getExceptionType(), detail, request, ex, null);
    }

    /**
     * Handles validation errors from @Valid annotation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetailDto> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );
        String detail = Translator.translate(ExceptionType.VALIDATION_FAILED.getMessageKey());
        return buildProblemDetail(ExceptionType.VALIDATION_FAILED, detail, request, ex, validationErrors);
    }

    /**
     * Handles malformed JSON requests.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetailDto> handleMalformedJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String detail;
        if (ex.getCause() instanceof InvalidFormatException cause) {
            // Provides a more specific message if possible.
            detail = Translator.translate(ExceptionType.TYPE_MISMATCH.getMessageKey(), cause.getValue(), cause.getTargetType().getSimpleName());
        } else {
            detail = Translator.translate(ExceptionType.MALFORMED_JSON.getMessageKey());
        }
        return buildProblemDetail(ExceptionType.MALFORMED_JSON, detail, request, ex, null);
    }

    /**
     * Handles missing required request parameters (e.g., @RequestParam).
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetailDto> handleMissingRequestParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String detail = Translator.translate(ExceptionType.MISSING_REQUEST_PARAM.getMessageKey(), ex.getParameterName());
        return buildProblemDetail(ExceptionType.MISSING_REQUEST_PARAM, detail, request, ex, null);
    }

    /**
     * Handles type mismatch errors for path variables or request parameters.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetailDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String detail = Translator.translate(ExceptionType.TYPE_MISMATCH.getMessageKey(), ex.getName(), ex.getRequiredType().getSimpleName());
        return buildProblemDetail(ExceptionType.TYPE_MISMATCH, detail, request, ex, null);
    }

    /**
     * Handles requests for non-existent endpoints.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetailDto> handleNotFound(NoResourceFoundException ex, HttpServletRequest request) {
        String detail = Translator.translate(ExceptionType.RESOURCE_NOT_FOUND.getMessageKey(), request.getRequestURI());
        return buildProblemDetail(ExceptionType.RESOURCE_NOT_FOUND, detail, request, ex, null);
    }

    /**
     * A final catch-all for any other unhandled exceptions, ensuring no error goes unhandled.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetailDto> handleAllUncaughtExceptions(Exception ex, HttpServletRequest request) {
//        log.error("An unexpected and unhandled error occurred", ex);
//        String detail = Translator.translate(ExceptionType.INTERNAL_SERVER_ERROR.getMessageKey());
        String detail = ex.getLocalizedMessage();
        return buildProblemDetail(ExceptionType.INTERNAL_SERVER_ERROR, detail, request, ex, null);
    }

    /**
     * Centralized builder method for creating a standardized RFC 9457 Problem Details response.
     */
    private ResponseEntity<ProblemDetailDto> buildProblemDetail(
            IZarnabException type,
            String translatedDetail,
            HttpServletRequest request,
            Exception exception,
            Map<String, String> errors) {

        String traceId = Optional.ofNullable(tracer.currentSpan())
                .map(span -> span.context().traceId())
                .orElse("N/A");

        if (type.getStatus().is5xxServerError()) {
            log.error("Server Error [traceId={}, errorCode={}] for request {} {}:", traceId, type.getCode(), request.getMethod(), request.getRequestURI(), exception);
        } else {
            log.warn("Client Error [traceId={}, errorCode={}] for request {} {}: {}", traceId, type.getCode(), request.getMethod(), request.getRequestURI(), exception.getMessage());
        }

        ProblemDetailDto problemDetail = new ProblemDetailDto(
                URI.create(ERROR_DOCS_BASE_URI + type.getCode()),
                type.getStatus().getReasonPhrase(),
                type.getStatus().value(),
                translatedDetail,
                URI.create(request.getRequestURI()),
                traceId,
                type.getCode(),
                errors
        );

        return new ResponseEntity<>(problemDetail, type.getStatus());
    }
}