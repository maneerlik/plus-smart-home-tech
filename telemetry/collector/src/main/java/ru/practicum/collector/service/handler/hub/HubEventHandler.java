package ru.practicum.collector.service.handler.hub;

import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.hub.enums.HubEventType;

public interface HubEventHandler {
    HubEventType getEventType();
    void handle(HubEvent event);
}
