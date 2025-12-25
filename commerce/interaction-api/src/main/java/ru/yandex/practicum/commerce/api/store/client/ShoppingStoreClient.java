package ru.yandex.practicum.commerce.api.store.client;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.api.store.dto.ProductDto;
import ru.yandex.practicum.commerce.api.store.dto.enums.ProductCategory;
import ru.yandex.practicum.commerce.api.store.dto.enums.QuantityState;

import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient {

    @GetMapping
    Page<ProductDto> getProducts(@RequestParam ProductCategory category,
                                 @PageableDefault Pageable pageable) throws FeignException;

    @PutMapping
    ProductDto createNewProduct(@RequestBody @Valid ProductDto productDto) throws FeignException;

    @PostMapping
    ProductDto updateProduct(@RequestBody @Valid ProductDto productDto) throws FeignException;

    @PostMapping("/removeProductFromStore")
    Boolean removeProductFromStore(@RequestBody String productId) throws FeignException;

    @PostMapping("/quantityState")
    Boolean setProductQuantityState(@RequestParam UUID productId,
                                    @RequestParam QuantityState quantityState) throws FeignException;

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable("productId") UUID productId) throws FeignException;

}
