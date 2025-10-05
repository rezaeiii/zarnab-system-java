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
    private Long totalTransfers;
    private Long transfersLastMonth;
}
