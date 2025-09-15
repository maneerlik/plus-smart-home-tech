package ru.practicum.collector.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.collector.exception.UnknownEventTypeException;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.hub.enums.HubEventType;
import ru.practicum.collector.model.sensor.SensorEvent;
import ru.practicum.collector.model.sensor.SensorEventType;
import ru.practicum.collector.service.handler.hub.HubEventHandler;
import ru.practicum.collector.service.handler.sensor.SensorEventHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Validated
@RestController
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class EventController {
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    public EventController(
            List<SensorEventHandler> sensorEventHandlers,
            List<HubEventHandler> hubEventHandlers
    ) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getEventType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getEventType, Function.identity()));
    }


    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public void addSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        SensorEventType eventType = sensorEvent.getType();
        SensorEventHandler handler = sensorEventHandlers.get(eventType);

        if (isNull(handler)) throw new UnknownEventTypeException("Unknown sensor event type: " + eventType);
        handler.handle(sensorEvent);
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void addHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        HubEventType eventType = hubEvent.getType();
        HubEventHandler handler = hubEventHandlers.get(eventType);

        if (isNull(handler)) throw new UnknownEventTypeException("Unknown hub event type: " + eventType);
        handler.handle(hubEvent);
    }
}
