package ru.yandex.practicum.analyzer.mapper.entity;

import ru.yandex.practicum.analyzer.model.Sensor;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;

public class SensorMapper {
    public static Sensor toSensor(DeviceAddedEventAvro avro, String hubId) {
        Sensor sensor = new Sensor();

        sensor.setId(avro.getId());
        sensor.setHubId(hubId);

        return sensor;
    }
}
