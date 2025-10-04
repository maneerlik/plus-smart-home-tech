package ru.yandex.practicum.collector.service.handler.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventHandler {
    HubEventProto.PayloadCase getEventType();

    void handle(HubEventProto event);
}
