package ru.practicum.collector.service.handler.sensor.impl;

import org.springframework.stereotype.Component;
import ru.practicum.collector.service.handler.EventProducer;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorAvro> {
    public LightSensorEventHandler(EventProducer eventProducer) {
        super(eventProducer, SensorEventProto.PayloadCase.LIGHT_SENSOR);
    }


    @Override
    public void handle(SensorEventProto event) {
        eventProducer.sendSensorEvent(mapToSensorEventAvro(event));
    }

    @Override
    protected LightSensorAvro mapToAvro(SensorEventProto event) {
        LightSensorProto lightSensorEvent = event.getLightSensor();
        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightSensorEvent.getLinkQuality())
                .setLuminosity(lightSensorEvent.getLuminosity())
                .build();
    }
}
