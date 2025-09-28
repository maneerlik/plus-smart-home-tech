package ru.practicum.collector.service.handler.sensor.impl;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.service.handler.EventProducer;
import ru.practicum.collector.service.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    protected final EventProducer eventProducer;
    protected final SensorEventProto.PayloadCase sensorEventType;


    protected SensorEventAvro mapToSensorEventAvro(SensorEventProto event) {
        Instant timestamp = Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(),
                event.getTimestamp().getNanos()
        );

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(timestamp)
                .setPayload(mapToAvro(event))
                .build();
    }

    protected abstract T mapToAvro(SensorEventProto event);

    @Override
    public final SensorEventProto.PayloadCase getEventType() {
        return sensorEventType;
    }
}
