package org.example.backend_farmacia.service;

import lombok.RequiredArgsConstructor;
import org.example.backend_farmacia.config.JwtUtil;
import org.example.backend_farmacia.dto.AuthResponseDTO;
import org.example.backend_farmacia.dto.LoginRequest;
import org.example.backend_farmacia.dto.UserDTO;
import org.example.backend_farmacia.entity.User;
import org.example.backend_farmacia.exception.UserAlreadyExistsException;
import org.example.backend_farmacia.exception.UserNotFoundException;
import org.example.backend_farmacia.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthResponseDTO register(UserDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email já cadastrado" );
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), "USER");

        return new AuthResponseDTO(token, user.getUsername(), user.getEmail());
    }

    public AuthResponseDTO login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.email());

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.password(), userOpt.get().getPassword())) {
            throw new UserNotFoundException("Email ou senha inválidos");
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponseDTO(token, user.getUsername(), user.getEmail());
    }
}
