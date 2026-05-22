package com.zarnab.panel.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
    private long totalCustomers;
    private double newCustomersLastMonth;
    private double totalAssetWeight;
    private double assetWeightInZarnab;
    private double assetWeightInCustomers;
    private double assetPriceInZarnab;
    private Long totalTransfers;
    private Long totalTransfersInput;
    private Long totalTransfersOutput;
    private double transfersLastMonth;

    private Long fullCoinCount;
    private Long halfCoinCount;
    private Long quarterCoinCount;
    private Long grammyCoinCount;

    private Double totalGoldBarWeight;
    private Double totalSilverBarWeight;
}
