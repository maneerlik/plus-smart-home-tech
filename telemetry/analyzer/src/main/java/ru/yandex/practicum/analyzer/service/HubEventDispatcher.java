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

/**
 * Диспетчер событий
 * Отвечает за маршрутизацию входящих {@link HubEventAvro} событий к соответствующим обработчикам
 * на основе типа полезной нагрузки ({@code payload})
 * <p>
 * При инициализации автоматически собирает все бины, реализующие интерфейс {@link HubEventHandler},
 * и регистрирует их в мапу, key - тип обрабатываемого сообщения {@link HubEventHandler#getMessageType()},
 * value - сам обработчик
 * <p>
 * Метод {@link #dispatch(HubEventAvro)} извлекает класс полезной нагрузки из события
 * и передаёт событие зарегистрированному обработчику, если таковой существует
 * <p>
 * Класс является потокобезопасным
 *
 * @see HubEventHandler
 * @see HubEventAvro
 */

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
