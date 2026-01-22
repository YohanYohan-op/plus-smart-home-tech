package ru.yandex.practicum.commerce.api.warehouse.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DimensionDto {

    @DecimalMin(value = "1.0", message = "Must be greater than 0.")
    private double width;

    @DecimalMin(value = "1.0", message = "Must be greater than 0.")
    private double height;

    @DecimalMin(value = "1.0", message = "Must be greater than 0.")
    private double depth;

}
