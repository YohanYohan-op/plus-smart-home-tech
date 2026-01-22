package ru.yandex.practicum.commerce.api.exception.payment;

public class NotEnoughInfoInOrderToCalculateException extends RuntimeException {
    public NotEnoughInfoInOrderToCalculateException(String message) { super(message); }
}
