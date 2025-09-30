package ru.yandex.practicum.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.service.handler.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HubEventDispatcher {
    private final Map<Class<?>, HubEventHandler<?>> handlers;

    public HubEventDispatcher(Set<HubEventHandler<?>> handlerSet) {
        this.handlers = handlerSet.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }


    public void dispatch(HubEventAvro event) {
        log.info("Dispatch event payload {}", event.getPayload());

        if (Objects.isNull(event.getPayload())) {
            log.error("HubEventAvro or payload is null");
            return;
        }

        Object payload = event.getPayload();
        HubEventHandler<?> handler = handlers.get(payload.getClass());

        if (Objects.isNull(handler)) {
            log.warn("No handler found for payload class: {}", payload.getClass().getName());
            return;
        }

        try {
            handler.handle(event);
        } catch (Exception e) {
            log.error("Error while handling event with payload {}: {}", payload.getClass().getName(), e.getMessage(), e);
        }
    }
}
