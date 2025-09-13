package ru.practicum.collector.service.handler.sensor;

import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.model.sensor.SensorEventType;

public interface SensorEventHandler {
    SensorEventType getEventType();
    void handle(SensorEvent event);
}
