package com.zarnab.panel.dashboard.service;

import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.core.util.RoleUtil;
import com.zarnab.panel.dashboard.dto.DashboardResponse;
import com.zarnab.panel.dashboard.dto.DashboardStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;

    @Override
    public DashboardResponse getDashboardData() {
        LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isAdminOrCounter = RoleUtil.hasRole(currentUser, Role.ADMIN, Role.COUNTER);

        DashboardStatsDto stats;
        if (isAdminOrCounter) {
            stats = userRepository.getDashboardStats(lastMonth);
        } else {
            stats = userRepository.getCustomerDashboardStats(currentUser, lastMonth);
        }

        double totalAssetWeight = stats.getTotalAssetWeight();
        double assetWeightInZarnab = stats.getAssetWeightInZarnab();
        double assetWeightInCustomers = totalAssetWeight - assetWeightInZarnab;

        // For customers, assetWeightInZarnab is 0, so assetWeightInCustomers equals their total assets.
        if (!isAdminOrCounter) {
            assetWeightInCustomers = totalAssetWeight;
        }

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
