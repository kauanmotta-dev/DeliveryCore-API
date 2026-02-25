package com.douradelivery.after.model.order.dto;

import com.douradelivery.after.model.order.enums.OrderEventType;

public record OrderStatusEventDTO(
        OrderEventType type,
        Long orderId,
        String status
) {}
