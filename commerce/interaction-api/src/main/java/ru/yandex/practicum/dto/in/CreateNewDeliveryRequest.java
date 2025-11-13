package ru.yandex.practicum.dto.in;

import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.OrderDto;

public record CreateNewDeliveryRequest(
        OrderDto orderDto,
        AddressDto fromAddress,
        AddressDto toAddress
) {
}
