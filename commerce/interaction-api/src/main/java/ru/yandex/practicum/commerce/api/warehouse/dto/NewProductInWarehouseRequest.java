package ru.yandex.practicum.commerce.api.warehouse.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewProductInWarehouseRequest {

    @NotNull(message = "The ID must not be empty.")
    private UUID productId;

    @NotNull(message = "The fragile should not be empty.")
    private boolean fragile;

    @NotNull(message = "The dimension should not be empty.")
    private DimensionDto dimension;

    @DecimalMin(value = "1.0", message = "The weight must be greater than 0.")
    private double weight;

}
