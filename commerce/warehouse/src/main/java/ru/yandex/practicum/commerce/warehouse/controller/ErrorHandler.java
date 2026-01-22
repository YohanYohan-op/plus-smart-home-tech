package ru.yandex.practicum.commerce.warehouse.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.api.exception.warehouse.InternalServerErrorException;
import ru.yandex.practicum.commerce.api.exception.warehouse.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.api.exception.warehouse.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.api.exception.warehouse.SpecifiedProductAlreadyInWarehouseException;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleSpecifiedProductAlreadyInWarehouseException(SpecifiedProductAlreadyInWarehouseException ex) {
        log.error(ex.getMessage(), ex.getLocalizedMessage());
        return errorResponse(HttpStatus.BAD_REQUEST, "product already registered", ex);
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoSpecifiedProductInWarehouseException(NoSpecifiedProductInWarehouseException ex) {
        log.error(ex.getMessage(), ex.getLocalizedMessage());
        return errorResponse(HttpStatus.BAD_REQUEST, "product not found", ex);
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleProductInShoppingCartLowQuantityInWarehouse(ProductInShoppingCartLowQuantityInWarehouse ex) {
        log.error(ex.getMessage(), ex.getLocalizedMessage());
        return errorResponse(HttpStatus.BAD_REQUEST, "product not enough", ex);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerErrorException(Throwable ex) {
        log.error(ex.getMessage(), ex.getLocalizedMessage());
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex);
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

