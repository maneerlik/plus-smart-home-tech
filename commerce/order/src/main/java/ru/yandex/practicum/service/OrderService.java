package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.in.CreateNewOrderRequest;
import ru.yandex.practicum.dto.in.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderDto> getUserOrders(String username);

    OrderDto createNewOrder(String username, CreateNewOrderRequest request);

    OrderDto returnProducts(ProductReturnRequest request);

    OrderDto payOrder(UUID orderId);

    OrderDto successfulPayment(UUID orderId);

    OrderDto paymentFailed(UUID orderId);

    OrderDto successfulDelivery(UUID orderId);

    OrderDto deliveryFailed(UUID orderId);

    OrderDto completeOrder(UUID orderId);

    OrderDto calculateTotal(UUID orderId);

    OrderDto calculateDelivery(UUID orderId);

    OrderDto assembleOrder(UUID orderId);

    OrderDto assemblyFailed(UUID orderId);
}
