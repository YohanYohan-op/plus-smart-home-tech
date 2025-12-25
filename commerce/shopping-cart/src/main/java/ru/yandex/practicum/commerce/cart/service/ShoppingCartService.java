package ru.yandex.practicum.commerce.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.api.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.api.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.cart.entity.ShoppingCart;
import ru.yandex.practicum.commerce.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.cart.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.commerce.cart.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    public ShoppingCartDto getShoppingCart(String username) {
        log.debug("Getting shopping cart info of user: {}", username);
        validateUsername(username);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsernameAndActive(username, true)
                .orElseGet(() -> createNewShoppingCart(username));
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Transactional
    public ShoppingCartDto addProducts(String username, Map<UUID, Integer> products) {
        log.debug("Adding products by user: {}", username);
        validateUsername(username);
        ShoppingCart shoppingCart = getActiveShoppingCart(username);
        products.forEach((key, value) -> shoppingCart.getProducts().merge(key, value, Integer::sum));
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Transactional
    public void deleteCart(String username) {
        validateUsername(username);
        ShoppingCart shoppingCart = getActiveShoppingCart(username);
        shoppingCart.setActive(false);
        shoppingCartRepository.save(shoppingCart);
        log.debug("Deactivating the shopping cart by user: {}", username);
    }

    @Transactional
    public ShoppingCartDto removeProducts(String username, List<UUID> products) {
        validateUsername(username);
        ShoppingCart shoppingCart = getActiveShoppingCart(username);
        products.forEach(key -> {
            shoppingCart.getProducts().remove(key);
        });
        shoppingCartRepository.save(shoppingCart);
        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toShoppingCartDto(shoppingCart);
        log.debug("User {} new shopping cart: {}", username, shoppingCartDto);
        return shoppingCartDto;
    }

    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        validateUsername(username);
        ShoppingCart shoppingCart = getActiveShoppingCart(username);
        UUID productId = request.getProductId();
        int newQuantity = request.getNewQuantity();
        if (!shoppingCart.getProducts().containsKey(productId)) {
            throw new NoProductsInShoppingCartException("no in cart product: " + productId);
        }
        shoppingCart.getProducts().put(productId, newQuantity);
        shoppingCartRepository.save(shoppingCart);
        log.debug("Product quantity changed by user: {}", username);
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    private ShoppingCart getActiveShoppingCart(String username) {
        return shoppingCartRepository.findByUsernameAndActive(username, true)
                .orElseGet(() -> createNewShoppingCart(username));
    }

    @Transactional
    private void updateProductQuantity(ShoppingCart shoppingCart, UUID productId, int quantityChange) {
        shoppingCart.getProducts().merge(productId, quantityChange, (current, change) -> {
            int updatedQuantity = current + change;
            return updatedQuantity > 0 ? updatedQuantity : null;
        });
    }

    private void validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new NotAuthorizedUserException("Username is empty.");
        }
    }

    @Transactional
    private ShoppingCart createNewShoppingCart(String username) {
        log.debug("Creating new shopping cart by user: {}", username);
        ShoppingCart cart = ShoppingCart.builder()
                .username(username)
                .active(true)
                .products(new HashMap<>())
                .build();
        return shoppingCartRepository.save(cart);
    }

}
