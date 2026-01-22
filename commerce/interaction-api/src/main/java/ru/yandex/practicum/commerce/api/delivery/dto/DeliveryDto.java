package ru.yandex.practicum.commerce.api.delivery.dto;

import ru.yandex.practicum.commerce.api.delivery.dto.enums.DeliveryState;
import ru.yandex.practicum.commerce.api.warehouse.dto.AddressDto;

import java.util.UUID;

public record DeliveryDto(UUID deliveryId, AddressDto fromAddress, AddressDto toAddress, UUID orderId,
                          DeliveryState deliveryState) {
}
