package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.ProductPageDto;
import ru.yandex.practicum.dto.in.GetProductsParams;
import ru.yandex.practicum.dto.in.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.service.StoreService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-store")
public class ShoppingStoreController {
    private final StoreService service;

    @GetMapping
    public ProductPageDto getProducts(
            @RequestParam ProductCategory category,
            @PageableDefault(sort = "productName") Pageable pageable
    ) {
        GetProductsParams params = new GetProductsParams(category, pageable);
        return service.getProducts(params);
    }

    @PutMapping
    public ProductDto addProduct(@RequestBody @Valid ProductDto product) {
        return service.addProduct(product);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody @Valid ProductDto productDto) {
        return service.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public boolean removeProduct(@RequestBody UUID productId) {
        return service.deactivateProduct(productId);
    }

    @PostMapping("/quantityState")
    public boolean setQuantityState(
            @RequestParam QuantityState quantityState,
            @RequestParam UUID productId
    ) {
        SetProductQuantityStateRequest request = new SetProductQuantityStateRequest(productId, quantityState);
        return service.setQuantityState(request);
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        return service.getProductById(productId);
    }
}
