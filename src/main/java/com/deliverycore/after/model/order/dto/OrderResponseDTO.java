package com.deliverycore.after.model.order.dto;

import com.deliverycore.after.model.address.entity.Address;
import com.deliverycore.after.model.order.enums.OrderStatus;

public record OrderResponseDTO(
        Long id,
        Address originAddress,
        Address destinationAddress,
        OrderStatus status
) {}
