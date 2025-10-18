package com.zarnab.panel.ingot.dto.req;

import com.zarnab.panel.ingot.model.Purity;
import com.zarnab.panel.ingot.model.ProductType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BatchCreateRequest(
        @NotNull @Min(1) @Max(500)
        Integer count,

        @NotNull
        Double weight,

        @NotNull
        Purity purity,

        @NotNull
        ProductType productType,

        @NotNull
        LocalDate manufactureDate
) {
}
