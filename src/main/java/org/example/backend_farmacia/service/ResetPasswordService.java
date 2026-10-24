package org.example.backend_farmacia.service;


import lombok.RequiredArgsConstructor;
import org.example.backend_farmacia.entity.PasswordResetToken;
import org.example.backend_farmacia.entity.User;
import org.example.backend_farmacia.exception.InvalidResetCodeException;
import org.example.backend_farmacia.repository.PasswordResetTokenRepository;
import org.example.backend_farmacia.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private static final int CODE_EXPIRATION_MINUTES = 15;
    private static final int MAX_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private  final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void requestReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email.trim());
        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();

        tokenRepository.deleteByUser(user);

        String code = String.format("%06d", secureRandom.nextInt(1_000_000));

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setCodeHash(passwordEncoder.encode(code));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES));
        tokenRepository.save(token);

        emailService.sendPasswordResetCode(user.getEmail(), user.getUsername(), code, CODE_EXPIRATION_MINUTES);
    }

    @Transactional(noRollbackFor = InvalidResetCodeException.class)
    public void resetPassword(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(InvalidResetCodeException::new);

        PasswordResetToken token = tokenRepository.findFirstByUserAndUsedFalseOrderByCreatedAtDesc(user)
                .orElseThrow(InvalidResetCodeException::new);

        if (token.getExpiresAt().isBefore(LocalDateTime.now()) || token.getAttempts() >= MAX_ATTEMPTS) {
            throw new InvalidResetCodeException();
        }

        if (!passwordEncoder.matches(code, token.getCodeHash())) {
            token.setAttempts(token.getAttempts() + 1);
            tokenRepository.save(token);
            throw new InvalidResetCodeException();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);
    }

}
