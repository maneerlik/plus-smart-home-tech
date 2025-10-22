package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.ProductPageDto;
import ru.yandex.practicum.dto.in.GetProductsParams;
import ru.yandex.practicum.dto.in.SetProductQuantityStateRequest;

import java.util.UUID;

public interface StoreService {
    ProductDto addProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean deactivateProduct(UUID id);

    boolean setQuantityState(SetProductQuantityStateRequest request);

    ProductDto getProductById(UUID id);

    ProductPageDto getProducts(GetProductsParams getProductsParams);
}
