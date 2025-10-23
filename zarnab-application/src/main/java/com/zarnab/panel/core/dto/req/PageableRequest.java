package com.zarnab.panel.core.dto.req;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableRequest {

    @Parameter(description = "Page number (0-indexed)", example = "0")
    private int page = 0;

    @Parameter(description = "Number of items per page", example = "10")
    private int size = 10;

    @Parameter(description = "Filtering criteria. Format: `field:operator:value`. " +
            "Supported operators: EQUAL, NOT_EQUAL, LIKE, IN, BETWEEN, GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, IS_NULL, IS_NOT_NULL. " +
            "For BETWEEN, use `field:BETWEEN:value1:value2`. For IN, use `field:IN:value1,value2,value3`.",
            array = @ArraySchema(schema = @Schema(type = "string")),
            examples = {
                    @ExampleObject(name = "Filter by serial", value = "serial:LIKE:INGOT123"),
                    @ExampleObject(name = "Filter by karat", value = "karat:EQUAL:24")
            })
    private List<FilterRequest> filters;

    @Parameter(description = "Sorting criteria. Format: `field:direction`. Supported directions: ASC, DESC.",
            array = @ArraySchema(schema = @Schema(type = "string")),
            examples = {
                    @ExampleObject(name = "Sort by creation date", value = "createdAt:DESC"),
                    @ExampleObject(name = "Sort by serial", value = "serial:ASC")
            })
    private List<SortRequest> sorts;
}
