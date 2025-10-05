package com.zarnab.panel.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
    private long totalCustomers;
    private long newCustomersLastMonth;
    private double totalAssetWeight;
    private double assetWeightInZarnab;
    private double assetWeightInCustomers;
    private long totalTransfers;
    private long transfersLastMonth;
}
