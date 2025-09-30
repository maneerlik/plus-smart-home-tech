package ru.yandex.practicum.analyzer.service.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.mapper.entity.ScenarioMapper;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.service.handler.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

@Component
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubEventHandler<ScenarioAddedEventAvro> {
    private final ScenarioRepository repository;


    @Override
    public Class<ScenarioAddedEventAvro> getMessageType() {
        return ScenarioAddedEventAvro.class;
    }

    @Override
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro scenarioAddedAvro = (ScenarioAddedEventAvro) event.getPayload();
        Scenario scenario = ScenarioMapper.toScenario(event.getHubId(), scenarioAddedAvro);
        repository.save(scenario);
    }
}
