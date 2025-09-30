package ru.yandex.practicum.analyzer.service.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.analyzer.service.handler.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
@RequiredArgsConstructor
public class DeviceRemovedHandler implements HubEventHandler<DeviceRemovedEventAvro> {
    private final SensorRepository repository;


    @Override
    public Class<DeviceRemovedEventAvro> getMessageType() {
        return DeviceRemovedEventAvro.class;
    }

    @Override
    public void handle(HubEventAvro event) {
        DeviceRemovedEventAvro deviceRemovedEventAvro = (DeviceRemovedEventAvro) event.getPayload();
        String id = deviceRemovedEventAvro.getId();
        if (repository.existsById(id)) repository.deleteById(id);
    }
}
