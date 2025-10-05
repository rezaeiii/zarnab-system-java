package com.zarnab.panel.dashboard.service;

import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.dashboard.dto.DashboardResponse;
import com.zarnab.panel.dashboard.dto.DashboardStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;

    @Override
    public DashboardResponse getDashboardData() {
        LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
        DashboardStatsDto stats = userRepository.getDashboardStats(lastMonth);

        double totalAssetWeight = stats.getTotalAssetWeight();
        double assetWeightInZarnab = stats.getAssetWeightInZarnab();
        double assetWeightInCustomers = totalAssetWeight - assetWeightInZarnab;

        return DashboardResponse.builder()
                .totalCustomers(stats.getTotalCustomers())
                .newCustomersLastMonth(stats.getNewCustomersLastMonth())
                .totalAssetWeight(totalAssetWeight)
                .assetWeightInZarnab(assetWeightInZarnab)
                .assetWeightInCustomers(assetWeightInCustomers)
                .totalTransfers(stats.getTotalTransfers())
                .transfersLastMonth(stats.getTransfersLastMonth())
                .build();
    }
}
