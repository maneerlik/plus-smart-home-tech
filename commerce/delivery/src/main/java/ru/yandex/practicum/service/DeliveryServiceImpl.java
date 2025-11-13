package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.contract.OrderClient;
import ru.yandex.practicum.contract.WarehouseClient;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.in.CreateNewDeliveryRequest;
import ru.yandex.practicum.dto.in.ShippedToDeliveryRequest;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {
    private static final String WAREHOUSE_ADDRESS_1 = "ADDRESS_1";
    private static final String WAREHOUSE_ADDRESS_2 = "ADDRESS_2";

    private final DeliveryRepository repository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Override
    public DeliveryDto createDelivery(CreateNewDeliveryRequest request) {
        Delivery delivery = DeliveryMapper.toDelivery(request);
        delivery.setDeliveryId(UUID.randomUUID());

        delivery = repository.save(delivery);

        return DeliveryMapper.toDeliveryDto(delivery);
    }

    @Override
    public void successfulDelivery(UUID orderId) {
        Delivery delivery = getByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        repository.save(delivery);
        orderClient.successfulDelivery(orderId);
    }

    @Override
    public void pickProducts(UUID orderId) {
        Delivery delivery = getByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        ShippedToDeliveryRequest request = new ShippedToDeliveryRequest(orderId, delivery.getDeliveryId());
        warehouseClient.shipProducts(request);
        repository.save(delivery);
    }

    @Override
    public void failedDelivery(UUID orderId) {
        Delivery delivery = getByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        repository.save(delivery);
        orderClient.deliveryFailed(orderId);
    }

    @Override
    public BigDecimal calculateDeliveryCost(UUID deliveryId) {
        Delivery delivery = repository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery not found"));

        BigDecimal price = BigDecimal.valueOf(5.0);

        String fromStreet = delivery.getFromAddress().getStreet();
        String toStreet = delivery.getToAddress().getStreet();

        if (fromStreet.contains(WAREHOUSE_ADDRESS_2)) price = price.add(price.multiply(BigDecimal.valueOf(2)));
        if (fromStreet.contains(WAREHOUSE_ADDRESS_1)) price = price.add(price);
        if (delivery.isFragile()) price = price.add(price.multiply(BigDecimal.valueOf(0.2)));

        price = price.add(BigDecimal.valueOf(delivery.getDeliveryWeight()).multiply(BigDecimal.valueOf(0.3)));
        price = price.add(BigDecimal.valueOf(delivery.getDeliveryVolume()).multiply(BigDecimal.valueOf(0.2)));

        if (!fromStreet.equals(toStreet)) price = price.add(price.multiply(BigDecimal.valueOf(0.2)));

        return price;
    }

    private Delivery getByOrderId(UUID orderId) {
        return repository.findByOrderId(orderId).orElseThrow(() -> new NoDeliveryFoundException("Delivery not found"));
    }
}
