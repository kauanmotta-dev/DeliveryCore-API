package com.douradelivery.after.controller;


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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuários")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Criar novo usuário")
    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestBody @Valid UserCreateRequestDTO request) {

        UserResponseDTO response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Retorna dados do usuário")
    @GetMapping("/me")
    public UserResponseDTO getMe(@AuthenticationPrincipal User user) {
        return userService.getMe(user);
    }

    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX SÓ O NOME?? XXXXXXXXXXXXXXXXXX
    @Operation(summary = "Atualiza usuário")
    @PutMapping("/me")
    public UserResponseDTO updateMe(
            @AuthenticationPrincipal User user,
            @RequestBody UserUpdateResquestXXXXXXXXXXX dto
    ) {
        return userService.updateMe(user, dto);
    }

    @Operation(summary = "Altera senha atual para nova")
    @PutMapping("/me/password")
    public void updatePassword(
            @AuthenticationPrincipal User user,
            @RequestBody UserUpdatePasswordRequestDTO dto
    ) {
        userService.updatePassword(user, dto);
    }
}
