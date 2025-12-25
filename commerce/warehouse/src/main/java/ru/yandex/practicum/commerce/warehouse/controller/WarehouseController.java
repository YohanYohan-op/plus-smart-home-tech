package ru.yandex.practicum.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.api.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.api.warehouse.client.WarehouseClient;
import ru.yandex.practicum.commerce.api.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.api.warehouse.dto.AddressDto;
import ru.yandex.practicum.commerce.api.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.api.warehouse.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseClient {

    private final WarehouseService warehouseService;

    @Override
    public void addProduct(NewProductInWarehouseRequest request) {
        log.info("Request to add a product {}", request);
        warehouseService.addProduct(request);
    }

    @Override
    public BookedProductsDto checkProductCount(ShoppingCartDto shoppingCartDto) {
        log.info("Request to check the shopping cart {}", shoppingCartDto);
        return warehouseService.checkProductCount(shoppingCartDto);
    }

    @Override
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        log.info("Request to add a product quantity {}", request);
        warehouseService.addProductQuantity(request);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Requesting a warehouse address");
        return warehouseService.getWarehouseAddress();
    }

}