package ru.yandex.practicum.commerce.api.order.dto;

import ru.yandex.practicum.commerce.api.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.api.warehouse.dto.AddressDto;

public record CreateNewOrderRequest(ShoppingCartDto shoppingCart, AddressDto deliveryAddress) {
}
