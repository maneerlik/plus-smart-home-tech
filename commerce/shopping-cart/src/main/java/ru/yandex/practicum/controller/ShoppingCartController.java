package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contract.ShoppingCartClient;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.in.ChangeProductQuantityRequest;
import ru.yandex.practicum.service.CartService;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartClient {
    private final CartService cartService;

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String username) {
        return cartService.getCart(username);
    }

    @PutMapping
    public ShoppingCartDto addProductsToCart(
            @RequestParam String username,
            @RequestBody Map<UUID, Integer> products
    ) {
        return cartService.addProductsToCart(username, products);
    }

    @DeleteMapping
    public void deactivateShoppingCart(@RequestParam String username) {
        cartService.deactivateShoppingCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeProductsFromCart(
            @RequestParam String username,
            @RequestBody Set<UUID> uuids
    ) {
        return cartService.removeProductsFromCart(username, uuids);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantity(
            @RequestParam String username,
            @RequestBody ChangeProductQuantityRequest request
    ) {
        return cartService.changeQuantity(username, request);
    }
}
