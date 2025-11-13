package ru.yandex.practicum.contract;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.in.CreateNewOrderRequest;
import ru.yandex.practicum.dto.in.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {

    @GetMapping
    List<OrderDto> getUserOrders(@RequestParam String username);

    @PutMapping
    OrderDto createNewOrder(
            @RequestParam String username,
            @RequestBody CreateNewOrderRequest request
    );

    @PostMapping("/return")
    OrderDto returnProducts(@RequestBody ProductReturnRequest request);

    @PostMapping("/payment")
    OrderDto payOrder(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestBody UUID orderId);

    @PostMapping("/payment/success")
    OrderDto successfulPayment(@RequestBody UUID orderId);

    @PostMapping("/delivery/success")
    OrderDto successfulDelivery(@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestBody UUID orderId);

    @PostMapping("/completed")
    OrderDto completeOrder(@RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotal(@RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDelivery(@RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDto assembleOrder(@RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto assemblyFailed(@RequestBody UUID orderId);
}
