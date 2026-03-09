package com.douradelivery.after.controller;

import com.douradelivery.after.exception.response.ApiResponse;
import com.douradelivery.after.model.address.dto.AddressCreateRequestDTO;
import com.douradelivery.after.model.address.entity.Address;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.service.AddressService;
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