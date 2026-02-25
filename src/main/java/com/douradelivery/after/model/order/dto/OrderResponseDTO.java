package com.douradelivery.after.model.order.dto;

import com.douradelivery.after.model.order.enums.OrderStatus;

public record OrderResponseDTO(
        Long id,
        String originAddress,
        String destinationAddress,
        OrderStatus status
) {}
