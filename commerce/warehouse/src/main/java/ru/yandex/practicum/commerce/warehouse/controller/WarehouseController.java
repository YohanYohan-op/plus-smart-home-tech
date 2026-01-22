package ru.yandex.practicum.commerce.warehouse.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.api.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.api.warehouse.client.WarehouseClient;
import ru.yandex.practicum.commerce.api.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.api.warehouse.dto.AddressDto;
import ru.yandex.practicum.commerce.api.warehouse.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.api.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.api.warehouse.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.entity.OrderBooking;
import ru.yandex.practicum.commerce.warehouse.repository.WarehouseOrderRepository;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseClient {

    private final WarehouseService warehouseService;
    private final WarehouseOrderRepository warehouseOrderRepository;

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

    @Override
    public void returnToWarehouse(Map<UUID, Long> products) {
        log.info("Request for a return to the warehouse {}", products);
        warehouseService.returnProductsToWarehouse(products);
    }

    @Override
    public BookedProductsDto assemblyOrder(AssemblyProductsForOrderRequest request) throws FeignException {
        final ShoppingCartDto shoppingCartDto = new ShoppingCartDto(
                request.orderId(),
                request.products()
        );
        BookedProductsDto result = checkProductCount(shoppingCartDto);
        OrderBooking newBooking = new OrderBooking();
        newBooking.setOrderId(request.orderId());
        newBooking.setProducts(request.products());
        warehouseOrderRepository.save(newBooking);
        return result;
    }

}