package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.in.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.in.NewProductInWarehouseRequest;

public interface WarehouseService {
    void createProduct(NewProductInWarehouseRequest request);

    BookedProductsDto checkProductsQuantity(ShoppingCartDto cart);

    void addProductQuantity(AddProductToWarehouseRequest request);

    AddressDto getAddress();
}
