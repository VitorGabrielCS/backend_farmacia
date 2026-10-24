package org.example.backend_farmacia.service;

import lombok.RequiredArgsConstructor;
import org.example.backend_farmacia.dto.OrderItemRequestDTO;
import org.example.backend_farmacia.dto.OrderItemResponseDTO;
import org.example.backend_farmacia.dto.OrderRequestDTO;
import org.example.backend_farmacia.dto.OrderResponseDTO;
import org.example.backend_farmacia.entity.Order;
import org.example.backend_farmacia.entity.OrderItem;
import org.example.backend_farmacia.entity.Product;
import org.example.backend_farmacia.entity.User;
import org.example.backend_farmacia.exception.InsuffiecientStockException;
import org.example.backend_farmacia.exception.ProductNotFoundException;
import org.example.backend_farmacia.repository.OrderRepository;
import org.example.backend_farmacia.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponseDTO createOrder(User user, OrderRequestDTO request) {

        Order order = new Order();
        order.setUser(user);

        List<OrderItem> orderItems = request.items().stream()
                .map(itemRequest -> buildOrderItem(order, itemRequest))
                .collect(Collectors.toList());

        order.setItems(orderItems);
        orderRepository.save(order);

        return toResponseDTO(order);
    }

    private OrderItem buildOrderItem(Order order, OrderItemRequestDTO itemRequest) {
        Product product = productRepository.findById(itemRequest.productId())
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado"));

        if (product.getQuantity() < itemRequest.quantity()) {
            throw new InsuffiecientStockException("Estoque de " + product + "está vazio!");
        }

        product.setQuantity(product.getQuantity() - itemRequest.quantity());
        productRepository.save(product);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(itemRequest.quantity());
        item.setPrice(product.getPrice());
        return item;
    }

    private OrderResponseDTO toResponseDTO(Order order) {
        List<OrderItemResponseDTO> items = order.getItems().stream()
                .map(i -> new OrderItemResponseDTO(
                        i.getProduct().getProductId(),
                        i.getProduct().getProductName(),
                        i.getQuantity(),
                        i.getPrice()))
                .toList();

        BigDecimal total = items.stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrderResponseDTO(order.getOrderId(), order.getUser().getUserId(),
                order.getOrderDate(), items, total);
    }
}
