package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contract.DeliveryClient;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.in.CreateNewDeliveryRequest;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/delivery")
public class DeliveryController implements DeliveryClient {
    private final DeliveryService deliveryService;

    @Override
    @PutMapping
    public DeliveryDto createDelivery(@RequestBody CreateNewDeliveryRequest request) {
        return deliveryService.createDelivery(request);
    }

    @Override
    @PostMapping("/successful")
    public void successfulDelivery(@RequestBody UUID orderId) {
        deliveryService.successfulDelivery(orderId);
    }

    @Override
    @PostMapping("/picked")
    public void pickProducts(@RequestBody UUID orderId) {
        deliveryService.pickProducts(orderId);
    }

    @Override
    @PostMapping("/failed")
    public void failedDelivery(@RequestBody UUID orderId) {
        deliveryService.failedDelivery(orderId);
    }

    @Override
    @PostMapping("/cost")
    public BigDecimal calculateDeliveryCost(@RequestBody UUID deliveryId) {
        return deliveryService.calculateDeliveryCost(deliveryId);
    }
}
