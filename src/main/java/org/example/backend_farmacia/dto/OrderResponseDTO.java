package org.example.backend_farmacia.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long orderId,
        Long userId,
        LocalDateTime orderDate,
        List<OrderItemResponseDTO> items,
        BigDecimal total
) {
}
