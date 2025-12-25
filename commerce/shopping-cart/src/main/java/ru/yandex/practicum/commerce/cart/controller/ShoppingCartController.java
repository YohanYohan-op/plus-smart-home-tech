package ru.yandex.practicum.commerce.cart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.api.cart.client.ShoppingCartClient;
import ru.yandex.practicum.commerce.api.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.api.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.cart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartClient {

    private final ShoppingCartService shoppingCartService;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        log.info("A shopping cart request has been received for the user {}", username);
        return shoppingCartService.getShoppingCart(username);
    }

    @Override
    public ShoppingCartDto addToCart(Map<UUID, Integer> products, String username) {
        log.info("For the user {}, a request to add products", username);
        return shoppingCartService.addProducts(username, products);
    }

    @Override
    public void deleteCart(String username) {
        log.info("User {} shopping-cart deletion request", username);
        shoppingCartService.deleteCart(username);
    }

    @Override
    public ShoppingCartDto removeFromCart(List<UUID> products, String username) {
        log.info("Request to delete items from the user {} shopping cart", username);
        return shoppingCartService.removeProducts(username, products);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(ChangeProductQuantityRequest request, String username) {
        log.info("Request to change the quantity of a product {} per {} user {}",
                request.getProductId(), request.getNewQuantity(), username);
        return shoppingCartService.changeProductQuantity(username, request);
    }

}
