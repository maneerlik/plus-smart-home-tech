package ru.practicum.collector.service.handler.sensor.impl;

import org.springframework.stereotype.Component;
import ru.practicum.collector.service.handler.EventProducer;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
public class MotionSensorEventHandler extends BaseSensorEventHandler<MotionSensorAvro> {
    public MotionSensorEventHandler(EventProducer eventProducer) {
        super(eventProducer, SensorEventProto.PayloadCase.MOTION_SENSOR);
    }


    @Override
    public void handle(SensorEventProto event) {
        eventProducer.sendSensorEvent(mapToSensorEventAvro(event));
    }

    @Override
    protected MotionSensorAvro mapToAvro(SensorEventProto event) {
        MotionSensorProto motionSensorEvent = event.getMotionSensor();
        return MotionSensorAvro.newBuilder()
                .setMotion(motionSensorEvent.getMotion())
                .setLinkQuality(motionSensorEvent.getLinkQuality())
                .setVoltage(motionSensorEvent.getVoltage())
                .build();
    }
}
