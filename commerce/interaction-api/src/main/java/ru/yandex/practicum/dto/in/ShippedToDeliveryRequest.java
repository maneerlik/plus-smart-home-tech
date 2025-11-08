package ru.yandex.practicum.dto.in;

import java.util.UUID;

public record ShippedToDeliveryRequest(
        UUID orderId,
        UUID deliveryId
) {
}
