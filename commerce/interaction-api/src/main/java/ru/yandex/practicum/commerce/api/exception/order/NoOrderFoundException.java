package ru.yandex.practicum.commerce.api.exception.order;

public class NoOrderFoundException extends RuntimeException {
    public NoOrderFoundException(String message) { super(message); }
}