package ru.yandex.practicum.collector.service.handler.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {
    SensorEventProto.PayloadCase getEventType();
    void handle(SensorEventProto event);
}
