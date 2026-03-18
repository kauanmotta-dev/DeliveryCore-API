package com.deliverycore.after.model.order.dto;

import com.deliverycore.after.model.order.enums.OrderStatus;

import java.time.LocalDateTime;

public record OrderStatusHistoryResponseDTO(
        OrderStatus status,
        String changedByName,
        LocalDateTime changedAt
) {}
