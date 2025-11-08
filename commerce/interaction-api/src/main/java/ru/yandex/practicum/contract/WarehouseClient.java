package ru.yandex.practicum.contract;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.in.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.in.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.in.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.in.ShippedToDeliveryRequest;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouseException;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {
    @PutMapping
    void createProduct(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto checkProductsQuantity(@RequestBody ShoppingCartDto cart)
            throws ProductInShoppingCartLowQuantityInWarehouseException;

    @PostMapping("/add")
    void addProductQuantity(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/address")
    AddressDto getAddress();

    @PostMapping("/return")
    void returnBookedProducts(@RequestBody Map<UUID, Integer> products);

    @GetMapping("/assembly")
    BookedProductsDto assemblyProducts(@RequestBody AssemblyProductsForOrderRequest request);

    @PostMapping("/shipped")
    void shipProducts(@RequestBody ShippedToDeliveryRequest request);
}
