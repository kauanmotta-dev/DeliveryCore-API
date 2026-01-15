package com.douradelivery.after.controller;

import com.douradelivery.after.exception.response.ApiResponse;
import com.douradelivery.after.model.auth.dto.AuthRequestDTO;
import com.douradelivery.after.model.auth.dto.AuthResponseDTO;
import com.douradelivery.after.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login do usuário")
    @PostMapping("/login")
    public ApiResponse<AuthResponseDTO> login(
            @RequestBody @Valid AuthRequestDTO request) {

        return ApiResponse.success(authService.login(request));
    }
}
