package org.example.backend_farmacia.repository;

import org.example.backend_farmacia.entity.PasswordResetToken;
import org.example.backend_farmacia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findFirstByUserAndUsedFalseOrderByCreatedAtDesc(User user);

    void deleteByUser(User user);
}
