package ru.yandex.practicum.commerce.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.api.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.api.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.api.warehouse.dto.AddressDto;
import ru.yandex.practicum.commerce.api.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.api.warehouse.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.entity.BookedProducts;
import ru.yandex.practicum.commerce.warehouse.entity.WarehouseProduct;
import ru.yandex.practicum.commerce.api.exception.warehouse.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.api.exception.warehouse.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.api.exception.warehouse.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.mapper.WarehouseMapper;
import ru.yandex.practicum.commerce.warehouse.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    private static final String[] ADDRESSES = new String[] {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    @Transactional
    public void addProduct(NewProductInWarehouseRequest request) {
        warehouseRepository.findById(request.getProductId())
                .ifPresent(warehouseProduct -> {
                    throw new SpecifiedProductAlreadyInWarehouseException("Product already exists.");
                });

        WarehouseProduct product = warehouseMapper.toWarehouseProduct(request);
        warehouseRepository.save(product);
    }

    public BookedProductsDto checkProductCount(ShoppingCartDto shoppingCartDto) {
        BookedProducts bookedProducts = new BookedProducts();
        shoppingCartDto.products().forEach((productId, quantity) -> {
            WarehouseProduct warehouseProduct = findWarehouseProductById(productId);
            if(warehouseProduct.getQuantity() < quantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("There is not enough product in the warehouse " +
                                                                      productId + ". Needs " + quantity +
                                                                      " in stock" + warehouseProduct.getQuantity());
            }
            bookedProducts.setFragile(bookedProducts.getFragile() || warehouseProduct.getFragile());
            bookedProducts.setDeliveryWeight(bookedProducts.getDeliveryWeight() +
                                             warehouseProduct.getWeight() * quantity);
            bookedProducts.setDeliveryVolume(bookedProducts.getDeliveryVolume() +
                                             warehouseProduct.getWidth() * warehouseProduct.getHeight() *
                                             warehouseProduct.getDepth() * quantity);
        });
        return new BookedProductsDto(bookedProducts.getDeliveryWeight(),
                bookedProducts.getDeliveryVolume(), bookedProducts.getFragile());
    }

    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        WarehouseProduct warehouseProduct = findWarehouseProductById(request.getProductId());
        warehouseProduct.setQuantity(request.getQuantity());
        warehouseRepository.save(warehouseProduct);
    }

    public AddressDto getWarehouseAddress() {
        return new AddressDto(CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS);
    }

    @Transactional
    public void returnProductsToWarehouse(Map<UUID, Long> products) {
        List<WarehouseProduct> warehouseProducts = warehouseRepository.findAllById(products.keySet());
        if(warehouseProducts.isEmpty()) {
            return;
        }
        warehouseProducts.forEach(warehouseProduct ->
                warehouseProduct.setQuantity(warehouseProduct.getQuantity() +
                                             products.get(warehouseProduct.getProductId())));
        warehouseRepository.saveAll(warehouseProducts);
    }

    private WarehouseProduct findWarehouseProductById(UUID productId) {
        return warehouseRepository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Product " + productId +
                                                                              " not found in warehouse"));
    }
}
