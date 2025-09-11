package com.zarnab.panel.core.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

/**
 * A DTO representing a problem details object, compliant with RFC 9457.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetailDto {

    private final URI type;
    private final String title;
    private final int status;
    private final String detail;
    private final URI instance;
    private final String traceId;
    private final String timestamp = Instant.now().toString();
    private final Map<String, String> errors;
    private final int errorCode;

    public ProblemDetailDto(URI type,
                            String title,
                            int status,
                            String detail,
                            URI instance,
                            String traceId,
                            int errorCode,
                            Map<String, String> errors) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
        this.traceId = traceId;
        this.errors = errors;
        this.errorCode = errorCode;
    }
}