package com.zarnab.panel.ingot.dto.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BulkIngotStateChangeRequest(
        @NotEmpty
        List<String> serials,
        @NotNull
        boolean assign
) {
}
