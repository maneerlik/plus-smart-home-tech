package ru.yandex.practicum.dto.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.yandex.practicum.dto.DimensionDto;

import java.util.UUID;

public record NewProductInWarehouseRequest(
        @NotNull
        UUID productId,
        boolean fragile,

        @NotNull
        @Valid
        DimensionDto dimension,

        @NotNull
        @Positive
        double weight
) {
}
