package ru.yandex.practicum.analyzer.service.handler;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventHandler<T extends SpecificRecordBase> {
    Class<T> getMessageType();

    void handle(HubEventAvro event);
}
