package com.deliverycore.after.model.order.dto;

import com.deliverycore.after.model.address.dto.AddressCreateRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record OrderCreateRequestDTO(

        @NotNull
        Long originAddressId,

        @Valid
        @NotNull
        AddressCreateRequestDTO destinationAddress

) {}