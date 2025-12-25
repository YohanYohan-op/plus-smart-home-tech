package ru.yandex.practicum.commerce.api.cart.dto;

import java.util.Map;
import java.util.UUID;

public record ShoppingCartDto(UUID shoppingCartId, Map<UUID, Long> products) {
}
