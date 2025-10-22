package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contract.WarehouseClient;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.in.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.in.NewProductInWarehouseRequest;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.service.WarehouseService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/warehouse")
public class WarehouseController implements WarehouseClient {
    private final WarehouseService service;

    @PutMapping
    public void createProduct(@RequestBody @Valid NewProductInWarehouseRequest request) {
        service.createProduct(request);
    }

    @PostMapping("/check")
    public BookedProductsDto checkProductsQuantity(@RequestBody ShoppingCartDto cart)
            throws ProductInShoppingCartLowQuantityInWarehouseException {
        return service.checkProductsQuantity(cart);
    }

    @PostMapping("/add")
    public void addProductQuantity(@RequestBody AddProductToWarehouseRequest request) {
        service.addProductQuantity(request);
    }

    @GetMapping("/address")
    public AddressDto getAddress() {
        return service.getAddress();
    }
}
