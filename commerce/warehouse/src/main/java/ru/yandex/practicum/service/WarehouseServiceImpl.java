package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.in.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.in.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.in.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.in.ShippedToDeliveryRequest;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.model.OrderBooking;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.OrderBookingRepository;
import ru.yandex.practicum.repository.ProductRepository;

import java.security.SecureRandom;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    private final ProductRepository productRepository;
    private final OrderBookingRepository orderBookingRepository;

    @Transactional
    @Override
    public void createProduct(NewProductInWarehouseRequest request) {
        boolean exists = productRepository.existsById(request.productId());
        if (exists) {
            throw new SpecifiedProductAlreadyInWarehouseException("Product has already exists in the warehouse");
        }

        WarehouseProduct warehouseProduct = WarehouseProductMapper.toWarehouseProduct(request);

        productRepository.save(warehouseProduct);
    }

    @Override
    public BookedProductsDto checkProductsQuantity(ShoppingCartDto cart) {
        Set<UUID> uuids = cart.getProducts().keySet();
        List<WarehouseProduct> warehouseProducts = productRepository.findAllById(uuids);

        double volume = 0;
        double weight = 0;
        boolean fragile = false;

        for (WarehouseProduct product : warehouseProducts) {
            UUID uuid = product.getProductId();

            boolean isLessThanRequired = product.getQuantity() < cart.getProducts().get(uuid);
            if (isLessThanRequired) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(
                        "Product from the cart is not in the required quantity in warehouse");
            }

            Dimension dimension = product.getDimension();

            volume += dimension.volume();

            if (!fragile) fragile = product.isFragile();

            weight += product.getWeight();
        }

        return new BookedProductsDto(weight, volume, fragile);
    }

    @Transactional
    @Override
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        WarehouseProduct warehouseProduct = productRepository.findById(request.productId()).orElseThrow(() ->
                new NoSpecifiedProductInWarehouseException("There is no information about the product in the warehouse"));

        int total = warehouseProduct.getQuantity() + request.quantity();
        warehouseProduct.setQuantity(total);

        productRepository.save(warehouseProduct);
    }

    @Override
    public AddressDto getAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    @Override
    public void returnBookedProducts(Map<UUID, Integer> returnedProducts) {
        List<WarehouseProduct> warehouseProducts = productRepository.findByProductIdIn(returnedProducts.keySet());

        for (WarehouseProduct product : warehouseProducts) {
            int actualQuantity = product.getQuantity();
            int quantityForReturn = returnedProducts.get(product.getProductId());
            product.setQuantity(actualQuantity + quantityForReturn);
        }

        productRepository.saveAll(warehouseProducts);
    }

    @Override
    public BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request) {
        List<WarehouseProduct> warehouseProducts = productRepository.findByProductIdIn(request.products().keySet());

        aggregateProductsQuantity(warehouseProducts, request.products());
        productRepository.saveAll(warehouseProducts);

        createOrderBooking(request);

        return aggregateBookedProducts(request.products(), warehouseProducts);
    }

    @Override
    public void shipProducts(ShippedToDeliveryRequest request) {
        orderBookingRepository.findByOrderId(request.orderId())
                .ifPresent(booking -> {
                    booking.setDeliveryId(request.deliveryId());
                    orderBookingRepository.save(booking);
                });
    }

    private BookedProductsDto aggregateBookedProducts(
            Map<UUID, Integer> pickedProducts,
            List<WarehouseProduct> warehouseProducts
    ) {
        double volume = 0;
        double weight = 0;
        boolean fragile = false;

        for (WarehouseProduct product : warehouseProducts) {
            int quantity = pickedProducts.get(product.getProductId());
            Dimension dimension = product.getDimension();
            volume += dimension.volume() * quantity;
            if (!fragile) fragile = product.isFragile();
            weight += product.getWeight() * quantity;
        }

        return new BookedProductsDto(weight, volume, fragile);
    }

    private void aggregateProductsQuantity(
            List<WarehouseProduct> warehouseProducts,
            Map<UUID, Integer> pickedProducts
    ) {
        for (WarehouseProduct product : warehouseProducts) {
            int quantityInCart = pickedProducts.get(product.getProductId());
            int actualQuantity = product.getQuantity();

            if (actualQuantity < quantityInCart) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(
                        "Product from the cart is not in the required quantity in warehouse");
            }

            product.setQuantity(actualQuantity - quantityInCart);
        }
    }

    private void createOrderBooking(AssemblyProductsForOrderRequest request) {
        OrderBooking booking = new OrderBooking();
        booking.setProducts(request.products());
        booking.setOrderId(request.orderId());

        orderBookingRepository.save(booking);
    }
}
