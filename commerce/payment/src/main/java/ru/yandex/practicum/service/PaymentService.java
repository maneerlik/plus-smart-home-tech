package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentDto createPayment(OrderDto order);

    BigDecimal getTotalCost(OrderDto order);

    void successfulPayment(UUID paymentId);

    BigDecimal getProductCost(OrderDto order);

    void failedPayment(UUID paymentId);
}
