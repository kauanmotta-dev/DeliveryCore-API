package com.deliverycore.after.service;

import com.deliverycore.after.config.jwt.JwtService;
import com.deliverycore.after.exception.exceptions.BusinessException;
import com.deliverycore.after.model.auth.dto.AuthRequestDTO;
import com.deliverycore.after.model.auth.dto.AuthResponseDTO;
import com.deliverycore.after.model.user.entity.User;
import com.deliverycore.after.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
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