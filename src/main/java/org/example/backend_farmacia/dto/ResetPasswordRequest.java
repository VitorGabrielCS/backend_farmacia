package org.example.backend_farmacia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Código é obrigatório")
        @Pattern(regexp = "\\d{6}", message = "O código deve ter 6 dígitos")
        String code,

        @NotBlank(message = "Nova senha é obrigatória")
        @Size(min = 7, max = 50, message = "A senha deve ter de 6 a 50 caracteres")
        String newPassword
) {
}
