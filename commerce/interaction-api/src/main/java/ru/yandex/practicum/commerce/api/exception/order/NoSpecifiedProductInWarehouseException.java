package ru.yandex.practicum.commerce.api.exception.order;

public class NoSpecifiedProductInWarehouseException extends RuntimeException {
    public NoSpecifiedProductInWarehouseException(String message) { super(message); }
}
