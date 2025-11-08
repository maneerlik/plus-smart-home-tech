package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.contract.OrderClient;
import ru.yandex.practicum.contract.ShoppingStoreClient;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.enums.PaymentState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository repository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Override
    public PaymentDto createPayment(OrderDto order) {
        Payment payment = PaymentMapper.toPayment(order);
        validatePrices(payment.getProductPrice(), payment.getDeliveryPrice());

        BigDecimal total = payment.getTotalPrice();

        if (Objects.isNull(total)) {
            String errorMessage = "Not enough information in order to calculate: no information about total cost";
            throw new NotEnoughInfoInOrderToCalculateException(errorMessage);
        }

        payment.setPaymentId(UUID.randomUUID());
        payment = repository.save(payment);
        BigDecimal feeTotal = calculateFee(payment.getProductPrice());

        return PaymentMapper.toPaymentDto(payment, feeTotal);
    }

    @Override
    public BigDecimal getTotalCost(OrderDto order) {
        BigDecimal deliveryPrice = order.getDeliveryPrice();
        BigDecimal productPrice = order.getProductPrice();
        validatePrices(productPrice, deliveryPrice);
        BigDecimal fee = calculateFee(productPrice);

        return deliveryPrice.add(productPrice).add(fee);
    }

    @Override
    public void successfulPayment(UUID paymentId) {
        Payment payment = getPaymentById(paymentId);
        payment.setPaymentState(PaymentState.SUCCESS);

        repository.save(payment);

        orderClient.successfulPayment(payment.getOrderId());
    }

    @Override
    public void failedPayment(UUID paymentId) {
        Payment payment = getPaymentById(paymentId);
        payment.setPaymentState(PaymentState.FAILED);

        repository.save(payment);

        orderClient.paymentFailed(payment.getOrderId());
    }

    @Override
    public BigDecimal getProductCost(OrderDto order) {
        Map<UUID, Integer> products = order.getProducts();
        List<UUID> productsIds = products.keySet().stream().toList();
        Map<UUID, BigDecimal> priceById = shoppingStoreClient.getProductsPrice(productsIds);

        BigDecimal productsCost = BigDecimal.ZERO;

        for (Map.Entry<UUID, BigDecimal> entry : priceById.entrySet()) {
            int quantity = products.get(entry.getKey());
            BigDecimal price = entry.getValue();
            BigDecimal sumPrice = price.multiply(BigDecimal.valueOf(quantity));
            productsCost = productsCost.add(sumPrice);
        }

        return productsCost;
    }

    private BigDecimal calculateFee(BigDecimal productPrice) {
        return productPrice.multiply(BigDecimal.valueOf(0.10));
    }

    private Payment getPaymentById(UUID paymentId) {
        return repository.findById(paymentId).orElseThrow(() -> new NoOrderFoundException("Payment was not found"));
    }

    private void validatePrices(BigDecimal productPrice, BigDecimal deliveryPrice) {
        if (Objects.isNull(deliveryPrice)) {
            String errorMessage = "Not enough information in order to calculate: no information about delivery";
            throw new NotEnoughInfoInOrderToCalculateException(errorMessage);
        }

        if (Objects.isNull(productPrice)) {
            String errorMessage = "Not enough information in order to calculate: no information about products price";
            throw new NotEnoughInfoInOrderToCalculateException(errorMessage);
        }
    }
}
