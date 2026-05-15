package com.zarnab.panel.ingot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zarnab.panel.common.util.DateUtil;

import java.time.LocalDate;

public record MonthlyWeightTransferDto(
        int year,
        int month,
        Double totalWeight
) {
    @JsonProperty
    public String getYearMonthJalali() {
        if (year == 0 || month == 0) {
            return null;
        }
        return DateUtil.toJalali(LocalDate.of(year, month, 15)).substring(0, 7);
    }
}