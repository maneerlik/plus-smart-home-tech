package ru.yandex.practicum.dto.in;

import java.util.UUID;

public record ChangeProductQuantityRequest(
        UUID productId,
        Integer newQuantity
) {
}
