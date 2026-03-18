package com.deliverycore.after.controller;

import com.deliverycore.after.exception.response.ApiResponse;
import com.deliverycore.after.model.report.dto.FinancialReportDTO;
import com.deliverycore.after.service.ReportService;
import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "Relatório Financeiro")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "retorna o relatório financeiro entre datas")
    @GetMapping("/financial")
    public ApiResponse<FinancialReportDTO> getFinancialReport(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end
    ) {
        return ApiResponse.success(
                reportService.getFinancialReport(start, end)
        );
    }
}

