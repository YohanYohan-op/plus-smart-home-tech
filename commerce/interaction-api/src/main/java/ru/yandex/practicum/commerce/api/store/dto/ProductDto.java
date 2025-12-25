package ru.yandex.practicum.commerce.api.store.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.commerce.api.store.dto.enums.ProductCategory;
import ru.yandex.practicum.commerce.api.store.dto.enums.ProductState;
import ru.yandex.practicum.commerce.api.store.dto.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private UUID productId;

    @NotBlank
    private String productName;

    @NotBlank
    private String description;

    private String imageSrc;

    @NotNull
    private QuantityState quantityState;

    @NotNull
    private ProductState productState;

    private double rating;

    @NotNull(message = "The product category should not be empty.")
    private ProductCategory productCategory;

    @DecimalMin(value = "1.0", message = "The price of the product must be at least 1.")
    private BigDecimal price;

}