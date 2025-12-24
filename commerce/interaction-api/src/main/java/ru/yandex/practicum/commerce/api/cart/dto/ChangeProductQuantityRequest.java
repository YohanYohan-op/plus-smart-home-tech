package ru.yandex.practicum.commerce.api.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeProductQuantityRequest {

    @NotNull(message = "The product ID must not be empty.")
    private UUID productId;

    @Min(value = 0, message = "New quantity must greater than 0.")
    private int newQuantity;

}
