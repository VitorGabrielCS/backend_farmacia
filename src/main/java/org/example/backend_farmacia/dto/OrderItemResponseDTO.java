package org.example.backend_farmacia.dto;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice
) {
}
