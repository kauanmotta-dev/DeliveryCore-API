package com.deliverycore.after.model.deliveryLocation.dto;

public record DeliveryLocationEventDTO(

        Long orderId,
        Double latitude,
        Double longitude

) {}
