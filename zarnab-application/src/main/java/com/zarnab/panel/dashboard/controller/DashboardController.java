package com.zarnab.panel.dashboard.controller;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.core.annotations.PageableParam;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.dashboard.dto.DashboardResponse;
import com.zarnab.panel.dashboard.service.DashboardService;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.MonthlyWeightDashboard;
import com.zarnab.panel.ingot.dto.res.IngotPurityStatsDto;
import com.zarnab.panel.ingot.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final TransferService transferService;

    @GetMapping("/states")
    public DashboardResponse getDashboardData() {
        return dashboardService.getDashboardData();
    }

    @Operation(summary = "List transfers with pagination, filtering, and sorting")
    @GetMapping("/transfers/to-counter")
    public ResponseEntity<PageableResponse<IngotDtos.TransferDto>> getTransfers(
            @AuthenticationPrincipal User user,
            @PageableParam @ParameterObject PageableRequest pageableRequest) {
        return ResponseEntity.ok(transferService.getCounterTransfers(user, pageableRequest));
    }

    @Operation(summary = "Get total ingot weight transferred from COUNTER to USER grouped by month")
    @GetMapping("/transfers/counter-to-user-weights")
    public ResponseEntity<List<MonthlyWeightDashboard>> getCounterToUserMonthlyWeights(
            @AuthenticationPrincipal User user) throws AccessDeniedException {
        return ResponseEntity.ok(transferService.getMonthlyCounterToUserTransfers(user));
    }

    @Operation(summary = "Get ingots grouped by purity")
    @GetMapping("/ingots/by-purity")
    public ResponseEntity<List<IngotPurityStatsDto>> getIngotsByPurity() {
        return ResponseEntity.ok(dashboardService.getIngotsByPurity());
    }
}
