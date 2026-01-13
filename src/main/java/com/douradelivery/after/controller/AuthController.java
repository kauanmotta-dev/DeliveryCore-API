package com.douradelivery.after.controller;

import com.douradelivery.after.model.auth.dto.AuthRequestDTO;
import com.douradelivery.after.model.auth.dto.AuthResponseDTO;
import com.douradelivery.after.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody @Valid AuthRequestDTO request) {

        return ResponseEntity.ok(authService.login(request));
    }
}
