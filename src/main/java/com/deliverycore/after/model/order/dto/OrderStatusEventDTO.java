package com.deliverycore.after.model.order.dto;

import com.deliverycore.after.model.order.enums.OrderEventType;

public record OrderStatusEventDTO(
        OrderEventType type,
        Long orderId,
        String status
) {}
