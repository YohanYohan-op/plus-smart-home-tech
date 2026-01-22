package ru.yandex.practicum.commerce.api.order.dto.enums;

public enum OrderState {
    NEW,
    ON_PAYMENT,
    ON_DELIVERY,
    DONE,
    DELIVERED,
    ASSEMBLED,
    PAID,
    COMPLETED,
    DELIVERY_FAILED,
    ASSEMBLY_FAILED,
    PAYMENT_FAILED,
    PRODUCT_RETURNED,
    CANCELED
}
