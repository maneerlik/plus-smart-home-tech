package ru.practicum.collector.service.handler.hub.impl;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.hub.DeviceAddedEvent;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.hub.enums.HubEventType;
import ru.practicum.collector.service.handler.EventProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Component
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {
    public DeviceAddedEventHandler(EventProducer eventProducer) {
        super(eventProducer, HubEventType.DEVICE_ADDED);
    }


    @Override
    public void handle(HubEvent event) {
        eventProducer.sendHubEvent(mapToHubEventAvro(event));
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEvent event) {
        DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) event;
        return DeviceAddedEventAvro.newBuilder()
                .setId(deviceAddedEvent.getId())
                .setType(DeviceTypeAvro.valueOf(deviceAddedEvent.getDeviceType().name()))
                .build();
    }
}
