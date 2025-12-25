package ru.yandex.practicum.commerce.warehouse.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookedProducts {
    private Double deliveryWeight = 0.0;
    private Double deliveryVolume = 0.0;
    private Boolean fragile = false;
}