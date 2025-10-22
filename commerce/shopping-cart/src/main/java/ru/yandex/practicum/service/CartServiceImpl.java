package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.contract.WarehouseClient;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.in.ChangeProductQuantityRequest;
import ru.yandex.practicum.exception.NoProductsInCartException;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.model.CartProduct;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.CartProductRepository;
import ru.yandex.practicum.repository.CartRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartProductRepository productRepository;
    private final WarehouseClient warehouseClient;

    @Transactional
    @Override
    public ShoppingCartDto addProductsToCart(String username, Map<UUID, Integer> products) {
        Optional<ShoppingCart> cart = getActualCart(username);
        ShoppingCart shoppingCart = cart.orElseGet(() -> createShoppingCart(username));

        ShoppingCartDto shoppingCartDto = CartMapper.toShoppingCartDto(shoppingCart);
        shoppingCartDto.setProducts(products);
        warehouseClient.checkProductsQuantity(shoppingCartDto);

        saveProducts(shoppingCart, products);

        return getActualCart(username).map(CartMapper::toShoppingCartDto).orElse(null);
    }

    @Override
    public ShoppingCartDto getCart(String username) {
        return getActualCart(username)
                .map(cart -> {
                    ShoppingCartDto dto = CartMapper.toShoppingCartDto(cart);
                    dto.setProducts(CartMapper.toShoppingCartMap(cart.getProducts()));
                    return dto;
                })
                .orElse(null);
    }

    @Transactional
    @Override
    public void deactivateShoppingCart(String username) {
        Optional<ShoppingCart> cart = getActualCart(username);

        if (cart.isPresent()) {
            ShoppingCart shoppingCart = cart.get();
            shoppingCart.setActive(false);
            cartRepository.save(shoppingCart);
        }
    }

    @Transactional
    @Override
    public ShoppingCartDto removeProductsFromCart(String username, Set<UUID> uuids) {
        Optional<ShoppingCart> cart = getActualCart(username);

        if (cart.isEmpty()) return null;

        ShoppingCart shoppingCart = cart.get();

        List<CartProduct> products = shoppingCart.getProducts();
        Set<UUID> productIdsInCart = products.stream()
                .map(CartProduct::getProductId)
                .collect(Collectors.toSet());

        if (!productIdsInCart.containsAll(uuids)) {
            throw new NoProductsInCartException("Products not found in the shopping cart");
        }

        productRepository.removeByProductIdIn(uuids);

        return getActualCart(username).map(CartMapper::toShoppingCartDto).orElse(null);
    }

    @Transactional
    @Override
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        CartProduct cartProduct = productRepository.findByProductId(request.productId()).orElseThrow(() ->
                new NoProductsInCartException("Products not found in the shopping cart"));

        productRepository.save(cartProduct);
        Optional<ShoppingCart> shoppingCart = getActualCart(username);

        return shoppingCart.map(CartMapper::toShoppingCartDto).orElse(null);
    }

    private Optional<ShoppingCart> getActualCart(String username) {
        return cartRepository.findByUsernameAndIsActive(username, true);
    }

    private ShoppingCart createShoppingCart(String username) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setShoppingCartId(UUID.randomUUID());
        shoppingCart.setUsername(username);

        return shoppingCart;
    }

    private void saveProducts(ShoppingCart shoppingCart, Map<UUID, Integer> products) {
        List<CartProduct> cartProducts = CartMapper.toCartProductList(products, shoppingCart);
        shoppingCart.getProducts().addAll(cartProducts);
        cartRepository.save(shoppingCart);
    }
}
