package com.douradelivery.after.controller;

import com.douradelivery.after.exception.response.ApiResponse;
import com.douradelivery.after.model.sla.dto.UserSlaDTO;
import com.douradelivery.after.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Service Level Agreement")
@RestController
@RequestMapping("/admin/sla")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SlaController {

    private final UserRepository userRepository;

    @Operation(summary = "Retorna lista de Usuário e seu nível de segurança")
    @GetMapping("/users")
    public ApiResponse<List<UserSlaDTO>> listUserSla() {
        return ApiResponse.success(
                userRepository.findAll().stream()
                        .map(UserSlaDTO::from)
                        .toList()
        );
    }
}

