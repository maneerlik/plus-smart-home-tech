package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.model.CartProduct;
import ru.yandex.practicum.model.ShoppingCart;

import java.util.*;
import java.util.stream.Collectors;

public final class CartMapper {
    /**
     * Don't let anyone instantiate this class.
     */
    private CartMapper() {

    }

    public static ShoppingCartDto toShoppingCartDto(ShoppingCart cart) {
        return ShoppingCartDto.builder()
                .cartId(cart.getShoppingCartId())
                .products(toShoppingCartMap(cart.getProducts()))
                .build();
    }

    public static ShoppingCart toShoppingCart(ShoppingCartDto dto, String username) {
        if (Objects.isNull(dto)) return null;

        ShoppingCart cart = new ShoppingCart();
        if (Objects.nonNull(dto.cartId)) cart.setShoppingCartId(dto.cartId);
        cart.setUsername(username);

        return cart;
    }

    public static Map<UUID, Integer> toShoppingCartMap(List<CartProduct> cartProducts) {
        return cartProducts.stream().collect(Collectors.toMap(CartProduct::getProductId, CartProduct::getQuantity));
    }

    public static List<CartProduct> toCartProductList(Map<UUID, Integer> products, ShoppingCart cart) {
        return products.entrySet().stream()
                .map(entry -> {
                    CartProduct cartProduct = new CartProduct();
                    cartProduct.setProductId(entry.getKey());
                    cartProduct.setQuantity(entry.getValue());
                    cartProduct.setShoppingCart(cart);
                    return cartProduct;
                })
                .toList();
    }
}
