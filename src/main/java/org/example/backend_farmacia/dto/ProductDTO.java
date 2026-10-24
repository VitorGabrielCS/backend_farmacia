package org.example.backend_farmacia.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProductDTO {

    @NotBlank
    private String productName;

    @NotBlank
    private String category;

    @NotBlank
    private String description;

    @NotBlank
    private String brand;

    private String image;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal price;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal promoPrice;

    private boolean promo;

    @NotNull
    @Positive
    private int quantity;

}
