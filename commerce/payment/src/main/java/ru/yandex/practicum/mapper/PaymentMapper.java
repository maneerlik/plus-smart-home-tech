package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.enums.PaymentState;
import ru.yandex.practicum.model.Payment;

import java.math.BigDecimal;

public final class PaymentMapper {
    /**
     * Don't let anyone instantiate this class.
     */
    private PaymentMapper() {

    }

    public static Payment toPayment(OrderDto order) {
        Payment payment = new Payment();

        payment.setPaymentState(PaymentState.PENDING);
        payment.setDeliveryPrice(order.getDeliveryPrice());
        payment.setProductPrice(order.getProductPrice());
        payment.setTotalPrice(order.getTotalPrice());
        payment.setOrderId(order.getOrderId());

        return payment;
    }

    public static PaymentDto toPaymentDto(Payment payment, BigDecimal feeTotal) {
        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .totalPayment(payment.getTotalPrice())
                .deliveryTotal(payment.getDeliveryPrice())
                .productTotal(payment.getProductPrice())
                .feeTotal(feeTotal)
                .build();
    }
}
