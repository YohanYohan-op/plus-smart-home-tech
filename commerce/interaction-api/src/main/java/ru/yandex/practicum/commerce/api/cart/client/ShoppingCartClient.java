package ru.yandex.practicum.commerce.api.cart.client;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.api.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.api.cart.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart-service", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient {

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam @NotEmpty String username) throws FeignException;

    @PutMapping
    ShoppingCartDto addToCart(@RequestBody Map<UUID, Integer> products,
                              @RequestParam @NotEmpty String username) throws FeignException;

    @DeleteMapping
    void deleteCart(@RequestParam @NotEmpty String username) throws FeignException;

    @PostMapping("/remove")
    ShoppingCartDto removeFromCart(@RequestBody List<UUID> products,
                                   @RequestParam @NotEmpty String username) throws FeignException;

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestBody @Valid ChangeProductQuantityRequest request,
                                          @RequestParam @NotEmpty String username) throws FeignException;

}