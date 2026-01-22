package ru.yandex.practicum.commerce.api.warehouse.client;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.api.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.api.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.api.warehouse.dto.AddressDto;
import ru.yandex.practicum.commerce.api.warehouse.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.api.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.api.warehouse.dto.NewProductInWarehouseRequest;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse-service", path = "/api/v1/warehouse")
public interface WarehouseClient {

    @PutMapping
    void addProduct (@RequestBody @Valid NewProductInWarehouseRequest request) throws FeignException;

    @PostMapping("/check")
    BookedProductsDto checkProductCount(@RequestBody @Valid ShoppingCartDto shoppingCartDto) throws FeignException;

    @PostMapping("/add")
    void addProductQuantity(@RequestBody @Valid AddProductToWarehouseRequest request) throws FeignException;

    @GetMapping("/address")
    AddressDto getWarehouseAddress() throws FeignException;

    @PostMapping("/return")
    void returnToWarehouse(@Valid @RequestBody Map<UUID, Long> products) throws FeignException;

    @PostMapping("/assembly")
    BookedProductsDto assemblyOrder(AssemblyProductsForOrderRequest request) throws FeignException;

}
