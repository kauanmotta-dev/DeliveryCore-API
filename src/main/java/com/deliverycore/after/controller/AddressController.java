package com.deliverycore.after.controller;

import com.deliverycore.after.exception.response.ApiResponse;
import com.deliverycore.after.model.address.dto.AddressCreateRequestDTO;
import com.deliverycore.after.model.address.entity.Address;
import com.deliverycore.after.model.user.entity.User;
import com.deliverycore.after.service.AddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Addresses")
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ApiResponse<Address> createAddress(

            @AuthenticationPrincipal User user,
            @RequestBody @Valid AddressCreateRequestDTO dto
    ) {

        return ApiResponse.success(
                addressService.createAddress(user, dto)
        );
    }

    @GetMapping("/me")
    public ApiResponse<List<Address>> listMyAddresses(
            @AuthenticationPrincipal User user
    ) {

        return ApiResponse.success(
                addressService.listUserAddresses(user)
        );
    }
}