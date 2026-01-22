package ru.yandex.practicum.commerce.store.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.api.exception.shoppingstore.InternalServerErrorException;
import ru.yandex.practicum.commerce.api.exception.shoppingstore.ProductNotFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFoundException(Throwable ex) {
        log.error(ex.getMessage(), ex.getLocalizedMessage());
        return errorResponse(HttpStatus.NOT_FOUND, "Продукт не найден", ex);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerErrorException(Throwable ex) {
        log.error(ex.getMessage(), ex.getLocalizedMessage());
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера", ex);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable ex) {
        log.error(ex.getMessage(), ex.getLocalizedMessage());
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", ex);
    }

    private ErrorResponse errorResponse(HttpStatus status, String userMessage, Throwable ex) {
        return ErrorResponse.builder()
                .cause(ex.getCause())
                .httpStatus(status.name())
                .userMessage(userMessage)
                .message(ex.getMessage())
                .suppressed(List.of(ex.getSuppressed()))
                .localizedMessage(ex.getLocalizedMessage())
                .build();
    }
}
