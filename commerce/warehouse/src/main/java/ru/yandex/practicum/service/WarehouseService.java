package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.in.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.in.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.in.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.in.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void createProduct(NewProductInWarehouseRequest request);

    BookedProductsDto checkProductsQuantity(ShoppingCartDto cart);

    void addProductQuantity(AddProductToWarehouseRequest request);

    AddressDto getAddress();

    void returnBookedProducts(Map<UUID, Integer> returnedProducts);

    BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request);

    void shipProducts(ShippedToDeliveryRequest request);
}
