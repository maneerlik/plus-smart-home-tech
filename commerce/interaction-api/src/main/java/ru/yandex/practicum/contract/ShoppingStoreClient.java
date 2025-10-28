package ru.yandex.practicum.contract;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.ProductPageDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.QuantityState;

import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient {
    @GetMapping
    ProductPageDto getProducts(
            @RequestParam ProductCategory category,
            @PageableDefault(sort = "productName") Pageable pageable
    );

    @PutMapping
    ProductDto addProduct(@RequestBody @Valid ProductDto product);

    @PostMapping
    ProductDto updateProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    boolean removeProduct(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    boolean setQuantityState(
            @RequestParam QuantityState quantityState,
            @RequestParam UUID productId
    );

    @GetMapping("/{productId}")
    ProductDto getProductById(@PathVariable UUID productId);
}
