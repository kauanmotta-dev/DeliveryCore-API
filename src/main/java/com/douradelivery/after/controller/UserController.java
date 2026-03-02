package com.douradelivery.after.controller;


import com.douradelivery.after.exception.response.ApiResponse;
import com.douradelivery.after.model.user.dto.UserCreateRequestDTO;
import com.douradelivery.after.model.user.dto.UserResponseDTO;
import com.douradelivery.after.model.user.dto.UserUpdatePasswordRequestDTO;
import com.douradelivery.after.model.user.dto.UserUpdateResquestXXXXXXXXXXX;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuários")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Criar novo usuário")
    @PostMapping("/create")
    public ApiResponse<UserResponseDTO> createUser(
            @RequestBody @Valid UserCreateRequestDTO request) {
        return ApiResponse.success(userService.createUser(request));
    }


    @PreAuthorize("hasAnyRole('CLIENT', 'DELIVERYMAN')")
    @Operation(summary = "Retorna dados do usuário")
    @GetMapping("/me")
    public ApiResponse<UserResponseDTO> getMe(@AuthenticationPrincipal User user) {
        return ApiResponse.success(userService.getMe(user));
    }


    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX SÓ O NOME?? XXXXXXXXXXXXXXXXXX
    @PreAuthorize("hasAnyRole('CLIENT', 'DELIVERYMAN')")
    @Operation(summary = "Atualiza usuário")
    @PutMapping("/me")
    public ApiResponse<UserResponseDTO> updateMe(
            @AuthenticationPrincipal User user,
            @RequestBody UserUpdateResquestXXXXXXXXXXX dto
    ) {
        return ApiResponse.success(userService.updateMe(user, dto));
    }


    @PreAuthorize("hasAnyRole('CLIENT', 'DELIVERYMAN')")
    @Operation(summary = "Altera senha atual para nova")
    @PutMapping("/me/updatePassword")
    public ApiResponse<Void> updatePassword(
            @AuthenticationPrincipal User user,
            @RequestBody UserUpdatePasswordRequestDTO dto
    ) {
        userService.updatePassword(user, dto);
        return ApiResponse.success("Password updated successfully");
    }

    @Operation(summary = "Lista todos os Usuários")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<List<UserResponseDTO>> listAll() {
        return ApiResponse.success(userService.listAll());
    }

    @Operation(summary = "Ativa/desativa Usuário")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/changeStatus")
    public ApiResponse<Void> changeStatus(
            @PathVariable Long id,
            @RequestParam boolean status
    ) {
        userService.changeUserStatus(id, status);
        return ApiResponse.success("Status updated successfully");
    }


}
