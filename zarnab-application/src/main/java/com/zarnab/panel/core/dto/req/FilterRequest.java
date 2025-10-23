package com.zarnab.panel.core.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequest {
    private String field;
    private Operator operator;
    private String value;
    private String valueTo; // For BETWEEN operator

    public enum Operator {
        EQUAL, NOT_EQUAL, LIKE, IN, BETWEEN, GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, IS_NULL, IS_NOT_NULL
    }
}
