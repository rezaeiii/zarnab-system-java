package com.zarnab.panel.ingot.dto.res;

import java.util.List;

public record BatchCreateResponse(
        Long batchId,
        List<String> serials
) {
}
