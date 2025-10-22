package ru.yandex.practicum.contract;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.in.ChangeProductQuantityRequest;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient {
    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam String username);

    @PutMapping
    ShoppingCartDto addProductsToCart(
            @RequestParam String username,
            @RequestBody Map<UUID, Integer> productsMap
    );

    @DeleteMapping
    void deactivateShoppingCart(@RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto removeProductsFromCart(
            @RequestParam String username,
            @RequestBody Set<UUID> uuids
    );

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantity(
            @RequestParam String username,
            @RequestBody ChangeProductQuantityRequest request
    );
}
