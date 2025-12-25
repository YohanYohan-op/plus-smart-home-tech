package ru.yandex.practicum.commerce.store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.api.store.client.ShoppingStoreClient;
import ru.yandex.practicum.commerce.api.store.dto.ProductDto;
import ru.yandex.practicum.commerce.api.store.dto.enums.ProductCategory;
import ru.yandex.practicum.commerce.api.store.dto.enums.QuantityState;
import ru.yandex.practicum.commerce.store.service.ShoppingStoreService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreClient {

    private final ShoppingStoreService shoppingStoreService;

    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.info("Getting products by category {} and pageable {}", category, pageable);
        return shoppingStoreService.getProductsByCategory(category, pageable);
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        log.info("Creating product by ProductDto: {}", productDto);
        return shoppingStoreService.createNewProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("Update product: {}", productDto);
        return shoppingStoreService.updateProduct(productDto);
    }

    @Override
    public Boolean removeProductFromStore(String productId) {
        log.info("Remove product from store: {}", productId);
        return shoppingStoreService.removeProductFromStore(UUID.fromString(productId.replace("\"", "")));
    }

    @Override
    public Boolean setProductQuantityState(UUID productId, QuantityState quantityState) {
        log.info("Update product {} quantity: {}", productId, quantityState);
        return shoppingStoreService.setProductQuantityState(productId, quantityState);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.info("Getting product by id: {}", productId);
        return shoppingStoreService.getProduct(productId);
    }

}