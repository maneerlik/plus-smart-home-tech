package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.in.CreateNewDeliveryRequest;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {
    DeliveryDto createDelivery(CreateNewDeliveryRequest request);

    void successfulDelivery(UUID orderId);

    void pickProducts(UUID orderId);

    void failedDelivery(UUID orderId);

    BigDecimal calculateDeliveryCost(UUID deliveryId);
}
