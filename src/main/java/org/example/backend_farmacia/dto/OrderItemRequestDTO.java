package org.example.backend_farmacia.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequestDTO(
        @NotNull
        Long productId,

        @Positive
        int quantity
) {
}
