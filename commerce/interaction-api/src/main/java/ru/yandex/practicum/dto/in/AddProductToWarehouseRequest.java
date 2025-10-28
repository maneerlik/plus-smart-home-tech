package ru.yandex.practicum.dto.in;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddProductToWarehouseRequest(
        @NotNull
        UUID productId,

        @NotNull
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {
}
