package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contract.OrderClient;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.in.CreateNewOrderRequest;
import ru.yandex.practicum.dto.in.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/order")
public class OrderController implements OrderClient {
    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getUserOrders(@RequestParam String username) {
        return orderService.getUserOrders(username);
    }

    @PutMapping
    public OrderDto createNewOrder(
            @RequestParam String username,
            @RequestBody @Valid CreateNewOrderRequest request
    ) {
        return orderService.createNewOrder(username, request);
    }

    @PostMapping("/return")
    public OrderDto returnProducts(@RequestBody @Valid ProductReturnRequest request) {
        return orderService.returnProducts(request);
    }

    @PostMapping("/payment")
    public OrderDto payOrder(@RequestBody UUID orderId) {
        return orderService.payOrder(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto paymentFailed(@RequestBody UUID orderId) {
        return orderService.paymentFailed(orderId);
    }

    @PostMapping("/payment/success")
    public OrderDto successfulPayment(@RequestBody UUID orderId) {
        return orderService.successfulPayment(orderId);
    }

    @PostMapping("/delivery/success")
    public OrderDto successfulDelivery(@RequestBody UUID orderId) {
        return orderService.successfulDelivery(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailed(@RequestBody UUID orderId) {
        return orderService.deliveryFailed(orderId);
    }

    @PostMapping("/completed")
    public OrderDto completeOrder(@RequestBody UUID orderId) {
        return orderService.completeOrder(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotal(@RequestBody UUID orderId) {
        return orderService.calculateTotal(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDelivery(@RequestBody UUID orderId) {
        return orderService.calculateDelivery(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto assembleOrder(@RequestBody UUID orderId) {
        return orderService.assembleOrder(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailed(@RequestBody UUID orderId) {
        return orderService.assemblyFailed(orderId);
    }
}
