package com.zarnab.panel.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private Long totalCustomers;
    private Long newCustomersLastMonth;
    private Double totalAssetWeight;
    private Double assetWeightInZarnab;
    private Long totalTransfersFromToCustomerOrCounter;
    private Long totalTransfersInput;
    private Long totalTransfersOutput;
    private Long transfersLastMonth;

    // Coins
    private Long fullCoinCount;
    private Long halfCoinCount;
    private Long quarterCoinCount;
    private Long grammyCoinCount;

    // Bars
    private Double totalGoldBarWeight;
    private Double totalSilverBarWeight;
}
