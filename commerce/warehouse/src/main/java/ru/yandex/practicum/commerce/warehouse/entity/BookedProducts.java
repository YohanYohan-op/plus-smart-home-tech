package ru.yandex.practicum.commerce.warehouse.entity;

import lombok.Data;

@Data
public class BookedProducts {
    private Double deliveryWeight = 0.0;
    private Double deliveryVolume = 0.0;
    private Boolean fragile = false;
}