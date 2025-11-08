package ru.yandex.practicum.contract;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.in.CreateNewDeliveryRequest;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient {
    @PutMapping
    DeliveryDto createDelivery(@RequestBody CreateNewDeliveryRequest request);

    @PostMapping("/successful")
    void successfulDelivery(@RequestBody UUID orderId);

    @PostMapping("/picked")
    void pickProducts(@RequestBody UUID orderId);

    @PostMapping("/failed")
    void failedDelivery(@RequestBody UUID orderId);

    @PostMapping("/cost")
    BigDecimal calculateDeliveryCost(@RequestBody UUID deliveryId);
}
