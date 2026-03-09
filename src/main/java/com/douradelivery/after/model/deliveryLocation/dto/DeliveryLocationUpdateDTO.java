package com.douradelivery.after.model.deliveryLocation.dto;

import jakarta.validation.constraints.NotNull;

public record  DeliveryLocationUpdateDTO (
        @NotNull
        Double latitude,

        @NotNull
        Double longitude

) {}