package org.example.backend_farmacia.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequestDTO(
        @NotEmpty(message = "O pedido precisa ter ao menos um item")
        @Valid
        List<OrderItemRequestDTO> items
) {}
