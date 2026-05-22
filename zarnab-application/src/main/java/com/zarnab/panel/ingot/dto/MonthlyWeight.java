package com.zarnab.panel.ingot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zarnab.panel.common.util.DateUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import static com.zarnab.panel.common.translate.LanguageUtil.toPersianNumber;

@Getter
@Setter
public class MonthlyWeight {

    private final LocalDate date;
    private String jalaliDate;
    private String jalaliDateFriendly;
    private String monthName;
    private Double totalWeight;
    private Double totalPrice;

    public MonthlyWeight(int year, int month, Double totalWeight) {
        this.date = LocalDate.of(year, month, 1);
        this.jalaliDate = toPersianNumber(DateUtil.toJalali(date).substring(0, 7));
        this.monthName = DateUtil.getJalaliMonthName(Integer.parseInt(jalaliDate.substring(5, 7)));
        this.jalaliDateFriendly = String.format("%s %s", monthName, toPersianNumber(jalaliDate.substring(0, 4)));
        this.totalWeight = totalWeight;
    }

    @JsonProperty
    public String getYearMonthJalali() {
        if (date == null) {
            return null;
        }
        return DateUtil.toJalali(date).substring(0, 7);
    }

    public Double getTotalWeight() {
        return totalWeight == null ? 0 : totalWeight;
    }
}