package com.douradelivery.after.model.deliverymanVerification.dto;

import jakarta.validation.constraints.NotBlank;

public record DeliverymanVerificationRequestDTO(

        @NotBlank
        String documentCpf,

        @NotBlank
        String documentRg,

        @NotBlank
        String driverLicense,

        @NotBlank
        String vehicleType,

        @NotBlank
        String vehiclePlate,

        @NotBlank
        String selfieUrl,

        @NotBlank
        String documentFrontUrl,

        @NotBlank
        String documentBackUrl

) {}