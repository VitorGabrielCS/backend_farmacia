package org.example.backend_farmacia.dto;

public record AuthResponseDTO(
        String token,
        String username,
        String email
) {
}
