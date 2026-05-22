package com.zarnab.panel.ingot.dto.res;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class IngotPurityStatsDto {

    private final Integer karat;
    private final Double totalWeight;
    private Double totalPrice;
    private final Long count;

    public IngotPurityStatsDto(Integer karat, Double totalWeight, Long count) {
        this.karat = karat;
        this.totalWeight = totalWeight;
        this.count = count;
    }
}