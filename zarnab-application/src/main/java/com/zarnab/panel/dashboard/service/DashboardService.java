package com.zarnab.panel.dashboard.service;

import com.zarnab.panel.dashboard.dto.DashboardResponse;
import com.zarnab.panel.ingot.dto.res.IngotPurityStatsDto;

import java.util.List;

public interface DashboardService {
    DashboardResponse getDashboardData();

    List<IngotPurityStatsDto> getIngotsByPurity();
}
