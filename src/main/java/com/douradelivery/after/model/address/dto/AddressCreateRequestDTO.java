package com.douradelivery.after.model.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressCreateRequestDTO(

        @NotBlank
        String label,

        @NotBlank
        String street,

        @NotBlank
        String number,

        @NotBlank
        String neighborhood,

        @NotBlank
        String city,

        @NotBlank
        String state,

        @NotBlank
        String zipCode,

        @NotNull
        Double latitude,

        @NotNull
        Double longitude

) {}