package org.mobilehub.installment_service.controller;

import lombok.RequiredArgsConstructor;
import org.mobilehub.installment_service.dto.dashboard.DashboardOverviewDto;
import org.mobilehub.installment_service.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public DashboardOverviewDto getOverview() {
        return dashboardService.getOverview();
    }
}
