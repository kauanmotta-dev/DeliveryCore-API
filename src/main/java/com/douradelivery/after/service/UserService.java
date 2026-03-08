package com.douradelivery.after.service;

import com.douradelivery.after.exception.exceptions.BusinessException;
import com.douradelivery.after.model.user.dto.UserCreateRequestDTO;
import com.douradelivery.after.model.user.dto.UserResponseDTO;
import com.douradelivery.after.model.user.dto.UserUpdatePasswordRequestDTO;
import com.douradelivery.after.model.user.dto.UserUpdateResquestXXXXXXXXXXX;
import com.douradelivery.after.model.user.entity.User;
import com.douradelivery.after.model.user.enums.UserRole;
import com.douradelivery.after.repository.UserRepository;
import com.douradelivery.after.util.CpfValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO createUser(UserCreateRequestDTO request) {

        if (!CpfValidator.isValid(request.cpf())) {
            throw new BusinessException("invalid CPF");
        }

        if (userRepository.existsByCpf(request.cpf())) {
            throw new BusinessException("CPF already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already exists");
        }

        User user = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.cpf(),
                UserRole.CLIENT,
                true);

        User savedUser = userRepository.save(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCpf(),
                savedUser.getRole()
        );
    }
    @Transactional(readOnly = true)
    public UserResponseDTO getMe(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCpf(),
                user.getRole()
        );
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx so o nome?? xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public UserResponseDTO updateMe(User user, UserUpdateResquestXXXXXXXXXXX dto) {
        user.setName(dto.name());
        userRepository.save(user);

        return getMe(user);
    }

    public void updatePassword(User user, UserUpdatePasswordRequestDTO dto) {
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new BusinessException("Current password doesn't match");
        }
        if (passwordEncoder.matches(dto.newPassword(), user.getPassword())) {
            throw new BusinessException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    // ADMIN -----------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<UserResponseDTO> listAll() {
        return userRepository.findAll()
                .stream()
                .map(this::getMe)
                .toList();
    }


    public void changeUserStatus(Long id, boolean status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));

        user.setActive(status);
        userRepository.save(user);
    }



}
