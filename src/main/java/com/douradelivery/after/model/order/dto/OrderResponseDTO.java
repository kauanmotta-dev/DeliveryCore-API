package com.douradelivery.after.model.order.dto;

import com.douradelivery.after.model.address.entity.Address;
import com.douradelivery.after.model.order.enums.OrderStatus;

public record OrderResponseDTO(
        Long id,
        Address originAddress,
        Address destinationAddress,
        OrderStatus status
) {}
