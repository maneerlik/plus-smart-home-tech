package ru.yandex.practicum.dto.in;

import java.util.Map;
import java.util.UUID;

public record AssemblyProductsForOrderRequest(
        Map<UUID, Integer> products,
        UUID orderId
) {
}
