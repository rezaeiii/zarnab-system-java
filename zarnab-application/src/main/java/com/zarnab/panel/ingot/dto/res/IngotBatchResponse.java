package com.zarnab.panel.ingot.dto.res;

import java.time.LocalDate;

public record IngotBatchResponse(
        Long id,
        LocalDate manufactureDate,
        int ingotCount
) {
}
