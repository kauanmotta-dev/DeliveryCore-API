package com.deliverycore.after.model.report.dto;

import java.math.BigDecimal;

public record FinancialReportDTO(
        BigDecimal totalPaid,
        BigDecimal totalRefunded,
        BigDecimal totalFees,
        Long paidOrders,
        Long refundedOrders
) {}
