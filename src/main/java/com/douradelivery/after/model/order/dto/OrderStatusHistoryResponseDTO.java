package com.douradelivery.after.model.order.dto;

import com.douradelivery.after.model.order.enums.OrderStatus;

import java.time.LocalDateTime;

public record OrderStatusHistoryResponseDTO(
        OrderStatus status,
        String changedByName,
        LocalDateTime changedAt
) {}
