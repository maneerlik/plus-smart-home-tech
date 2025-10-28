package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.in.ChangeProductQuantityRequest;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface CartService {
    ShoppingCartDto addProductsToCart(String username, Map<UUID, Integer> products);

    ShoppingCartDto getCart(String username);

    void deactivateShoppingCart(String username);

    ShoppingCartDto removeProductsFromCart(String username, Set<UUID> uuids);

    ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request);
}
