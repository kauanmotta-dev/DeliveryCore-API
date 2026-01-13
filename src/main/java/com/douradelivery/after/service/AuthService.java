package com.douradelivery.after.service;

import com.douradelivery.after.config.jwt.JwtService;
import com.douradelivery.after.exception.BusinessException;
import com.douradelivery.after.model.auth.dto.AuthRequestDTO;
import com.douradelivery.after.model.auth.dto.AuthResponseDTO;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDTO login(AuthRequestDTO request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("Credenciais inválidas");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponseDTO(token);
    }
}