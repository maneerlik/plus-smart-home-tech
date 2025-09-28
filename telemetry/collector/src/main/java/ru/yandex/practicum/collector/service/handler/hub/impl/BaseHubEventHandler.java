package ru.yandex.practicum.collector.service.handler.hub.impl;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.collector.service.handler.EventProducer;
import ru.yandex.practicum.collector.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    protected final EventProducer eventProducer;
    protected final HubEventProto.PayloadCase hubEventType;


    protected HubEventAvro mapToHubEventAvro(HubEventProto event) {
        Instant timestamp = Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(),
                event.getTimestamp().getNanos()
        );

        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(timestamp)
                .setPayload(mapToAvro(event))
                .build();
    }

    protected abstract T mapToAvro(HubEventProto event);

    @Override
    public final HubEventProto.PayloadCase getEventType() {
        return hubEventType;
    }
}
