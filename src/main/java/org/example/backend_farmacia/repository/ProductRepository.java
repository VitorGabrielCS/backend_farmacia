package org.example.backend_farmacia.repository;

import org.example.backend_farmacia.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductNameAndCategory(String productName, String category);
}
