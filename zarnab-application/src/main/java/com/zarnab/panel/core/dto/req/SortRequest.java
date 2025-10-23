package com.zarnab.panel.core.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortRequest {
    private String field;
    private Direction direction;

    public enum Direction {
        ASC, DESC
    }
}
