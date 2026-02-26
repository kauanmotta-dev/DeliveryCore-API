package com.douradelivery.after.repository;

import com.douradelivery.after.model.order.entity.Order;
import com.douradelivery.after.model.payment.entity.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {


    Optional<Payment> findByOrder(Order order);

        @Query("""
        SELECT
            COALESCE(SUM(p.amount), 0),
            COALESCE(SUM(p.refundedAmount), 0),
            COALESCE(SUM(p.feeAmount), 0),
            COUNT(CASE WHEN p.status = 'PAID' THEN 1 END),
            COUNT(CASE WHEN p.status = 'REFUNDED' THEN 1 END)
        FROM Payment p
        WHERE p.createdAt BETWEEN :start AND :end
    """)
        Object[] getFinancialSummary(
                @Param("start") LocalDateTime start,
                @Param("end") LocalDateTime end
        );
}

