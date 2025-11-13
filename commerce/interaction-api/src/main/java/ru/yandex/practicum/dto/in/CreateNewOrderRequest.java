package ru.yandex.practicum.dto.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.ShoppingCartDto;

public record CreateNewOrderRequest(
        @NotNull
        @Valid
        ShoppingCartDto shoppingCart,

        @NotNull
        AddressDto deliveryAddress
) {
}
