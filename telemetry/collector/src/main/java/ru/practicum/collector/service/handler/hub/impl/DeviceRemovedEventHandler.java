package ru.practicum.collector.service.handler.hub.impl;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.hub.DeviceRemovedEvent;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.hub.enums.HubEventType;
import ru.practicum.collector.service.handler.EventProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Component
public class DeviceRemovedEventHandler extends BaseHubEventHandler<DeviceRemovedEventAvro> {
    public DeviceRemovedEventHandler(EventProducer eventProducer) {
        super(eventProducer, HubEventType.DEVICE_REMOVED);
    }


    @Override
    public void handle(HubEvent event) {
        eventProducer.sendHubEvent(mapToHubEventAvro(event));
    }

    @Override
    protected DeviceRemovedEventAvro mapToAvro(HubEvent event) {
        DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) event;
        return DeviceRemovedEventAvro.newBuilder()
                .setId(deviceRemovedEvent.getId())
                .build();
    }
}
