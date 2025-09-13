package ru.practicum.collector.service.handler.sensor.impl;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.service.handler.sensor.SensorEventHandler;
import ru.practicum.collector.service.handler.EventProducer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    protected final EventProducer eventProducer;


    protected SensorEventAvro mapToSensorEventAvro(SensorEvent event) {
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(mapToAvro(event))
                .build();
    }

    protected abstract T mapToAvro(SensorEvent event);
}
