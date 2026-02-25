package com.douradelivery.after.model.order.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderCreateRequestDTO(

        @NotBlank
        String originAddress,

        @NotBlank
        String destinationAddress
) {}
