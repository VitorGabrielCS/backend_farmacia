package org.example.backend_farmacia.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend_farmacia.dto.OrderRequestDTO;
import org.example.backend_farmacia.dto.OrderResponseDTO;
import org.example.backend_farmacia.entity.User;
import org.example.backend_farmacia.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO create(@AuthenticationPrincipal User user, @Valid @RequestBody OrderRequestDTO request) {
        return orderService.createOrder(user, request);
    }
}
