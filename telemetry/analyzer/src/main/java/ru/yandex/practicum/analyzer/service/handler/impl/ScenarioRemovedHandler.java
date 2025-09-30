package ru.yandex.practicum.analyzer.service.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.service.handler.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
@RequiredArgsConstructor
public class ScenarioRemovedHandler implements HubEventHandler<ScenarioRemovedEventAvro> {
    private final ScenarioRepository repository;


    @Override
    public Class<ScenarioRemovedEventAvro> getMessageType() {
        return ScenarioRemovedEventAvro.class;
    }

    @Override
    public void handle(HubEventAvro event) {
        ScenarioRemovedEventAvro scenarioRemovedEventAvro = (ScenarioRemovedEventAvro) event.getPayload();
        String name = scenarioRemovedEventAvro.getName();
        if (repository.existsByName(name)) repository.deleteByName(name);
    }
}
