package ru.yandex.practicum.dto.in;

import ru.yandex.practicum.enums.QuantityState;

import java.util.UUID;

public record SetProductQuantityStateRequest(
        UUID productId,
        QuantityState quantityState
) {
}
