package ru.practicum.collector.service.handler.sensor.impl;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.sensor.LightSensorEvent;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.model.sensor.SensorEventType;
import ru.practicum.collector.service.handler.EventProducer;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorAvro> {
    public LightSensorEventHandler(EventProducer eventProducer) {
        super(eventProducer, SensorEventType.LIGHT_SENSOR_EVENT);
    }


    @Override
    public void handle(SensorEvent event) {
        eventProducer.sendSensorEvent(mapToSensorEventAvro(event));
    }

    @Override
    protected LightSensorAvro mapToAvro(SensorEvent event) {
        LightSensorEvent lightSensorEvent = (LightSensorEvent) event;
        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightSensorEvent.getLinkQuality())
                .setLuminosity(lightSensorEvent.getLuminosity())
                .build();
    }
}
