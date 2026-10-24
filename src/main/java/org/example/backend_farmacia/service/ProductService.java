package org.example.backend_farmacia.service;

import lombok.RequiredArgsConstructor;
import org.example.backend_farmacia.dto.ProductDTO;
import org.example.backend_farmacia.entity.Product;
import org.example.backend_farmacia.exception.ProductAlreadyExistsException;
import org.example.backend_farmacia.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product createProduct(ProductDTO productDTO) {
        boolean productExist = productRepository.findByProductNameAndCategory(
                productDTO.getProductName(), productDTO.getCategory()).isPresent();

        if (productExist) {
            throw new ProductAlreadyExistsException("Esse produto já está cadastrado");
        }

        Product newProduct = Product.builder()
                .productName(productDTO.getProductName())
                .description(productDTO.getDescription())
                .category(productDTO.getCategory())
                .brand(productDTO.getBrand())
                .image(productDTO.getImage())
                .price(productDTO.getPrice())
                .promoPrice(productDTO.getPromoPrice())
                .promo(productDTO.isPromo())
                .quantity(productDTO.getQuantity())
                .build();

        return productRepository.save(newProduct);
    }
}
