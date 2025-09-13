package ru.practicum.collector.service.handler.hub.impl;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.hub.enums.HubEventType;
import ru.practicum.collector.service.handler.EventProducer;
import ru.practicum.collector.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    protected final EventProducer eventProducer;
    protected final HubEventType hubEventType;


    protected HubEventAvro mapToHubEventAvro(HubEvent event) {
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(mapToAvro(event))
                .build();
    }

    protected abstract T mapToAvro(HubEvent event);

    @Override
    public final HubEventType getEventType() {
        return hubEventType;
    }
}
