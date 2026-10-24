package org.example.backend_farmacia.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend_farmacia.dto.ForgotPasswordRequest;
import org.example.backend_farmacia.dto.ResetPasswordRequest;
import org.example.backend_farmacia.exception.InvalidResetCodeException;
import org.example.backend_farmacia.service.ResetPasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        resetPasswordService.requestReset(request.email());

        return ResponseEntity.ok(Map.of("message", "Se o e-mail estiver cadastrado, enviamos um código de 6 dígitos." ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordService.resetPassword(request.email(), request.code(), request.newPassword());
        return ResponseEntity.ok(Map.of("message", "A senha foi redefinida com sucesso."));
    }
    @ExceptionHandler(InvalidResetCodeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCode(InvalidResetCodeException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
