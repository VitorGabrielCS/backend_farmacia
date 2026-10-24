package org.example.backend_farmacia.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend_farmacia.dto.ProductDTO;
import org.example.backend_farmacia.entity.Product;
import org.example.backend_farmacia.repository.ProductRepository;
import org.example.backend_farmacia.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final ProductRepository productRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Product create(@Valid @RequestBody ProductDTO productDTO) {
        return productService.createProduct(productDTO);
    }

    @GetMapping
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product findBtId(@PathVariable Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new org.example.backend_farmacia.exception.ProductNotFoundException("Produto não encontrado: " + id));
    }
}
