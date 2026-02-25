package com.douradelivery.after.controller;

import com.douradelivery.after.exception.response.ApiResponse;
import com.douradelivery.after.model.sla.dto.UserSlaDTO;
import com.douradelivery.after.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/sla")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SlaController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public ApiResponse<List<UserSlaDTO>> listUserSla() {
        return ApiResponse.success(
                userRepository.findAll().stream()
                        .map(UserSlaDTO::from)
                        .toList()
        );
    }
}

