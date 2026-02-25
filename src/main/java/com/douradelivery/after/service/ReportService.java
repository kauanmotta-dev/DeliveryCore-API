package com.douradelivery.after.service;

import com.douradelivery.after.model.report.dto.FinancialReportDTO;
import com.douradelivery.after.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {

    private final PaymentRepository paymentRepository;

    public FinancialReportDTO getFinancialReport(
            LocalDateTime start,
            LocalDateTime end
    ) {

        Object[] result = paymentRepository.getFinancialSummary(start, end);

        return new FinancialReportDTO(
                (BigDecimal) result[0],
                (BigDecimal) result[1],
                (BigDecimal) result[2],
                (Long) result[3],
                (Long) result[4]
        );
    }
}
