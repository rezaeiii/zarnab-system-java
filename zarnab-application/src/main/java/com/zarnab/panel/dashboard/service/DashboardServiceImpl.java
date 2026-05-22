package com.zarnab.panel.dashboard.service;

import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.core.util.RoleUtil;
import com.zarnab.panel.dashboard.dto.DashboardResponse;
import com.zarnab.panel.dashboard.dto.DashboardStatsDto;
import com.zarnab.panel.ingot.dto.res.IngotPurityStatsDto;
import com.zarnab.panel.ingot.repository.IngotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final IngotRepository ingotRepository;

    @Value("${zarnab.gold-price}")
    private long goldPrice;

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
                .newCustomersLastMonth((stats.getTotalCustomers().floatValue() / stats.getNewCustomersLastMonth()))
                .totalAssetWeight(totalAssetWeight)
                .assetWeightInZarnab(assetWeightInZarnab)
                .assetPriceInZarnab(assetWeightInZarnab * goldPrice)
                .assetWeightInCustomers(assetWeightInCustomers)
                .totalTransfers(stats.getTotalTransfersFromToCustomerOrCounter())
                .totalTransfersInput(stats.getTotalTransfersInput())
                .totalTransfersOutput(stats.getTotalTransfersOutput())
                .transfersLastMonth(stats.getTotalTransfersFromToCustomerOrCounter().floatValue() / stats.getTransfersLastMonth())

                // Coins
                .fullCoinCount(stats.getFullCoinCount())
                .halfCoinCount(stats.getHalfCoinCount())
                .quarterCoinCount(stats.getQuarterCoinCount())
                .grammyCoinCount(stats.getGrammyCoinCount())

                // Bars
                .totalGoldBarWeight(stats.getTotalGoldBarWeight())
                .totalSilverBarWeight(stats.getTotalSilverBarWeight())
                .build();
    }


    @Override
    public List<IngotPurityStatsDto> getIngotsByPurity() {

        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        boolean isAdminOrCounter =
                RoleUtil.hasRole(currentUser, Role.ADMIN, Role.COUNTER);

        if (isAdminOrCounter) {
            return ingotRepository.getIngotsGroupedByPurity();
        }

        return ingotRepository.getIngotsGroupedByPurityForUser(currentUser);
    }
}
