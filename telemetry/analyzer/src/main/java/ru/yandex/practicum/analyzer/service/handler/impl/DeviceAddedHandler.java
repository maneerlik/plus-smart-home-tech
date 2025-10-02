package ru.yandex.practicum.analyzer.service.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.mapper.entity.SensorMapper;
import ru.yandex.practicum.analyzer.model.Sensor;
import ru.yandex.practicum.analyzer.service.SensorService;
import ru.yandex.practicum.analyzer.service.handler.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
@RequiredArgsConstructor
public class DeviceAddedHandler implements HubEventHandler<DeviceAddedEventAvro> {
    private final SensorService service;


    @Override
    public Class<DeviceAddedEventAvro> getMessageType() {
        return DeviceAddedEventAvro.class;
    }

    @Override
    public void handle(HubEventAvro event) {
        DeviceAddedEventAvro deviceAddedEventAvro = (DeviceAddedEventAvro) event.getPayload();
        Sensor sensor = SensorMapper.toSensor(deviceAddedEventAvro, event.getHubId());
        service.saveSensor(sensor);
    }
}
