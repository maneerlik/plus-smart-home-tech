package ru.practicum.collector.service.handler.hub.impl;

import org.springframework.stereotype.Component;
import ru.practicum.collector.mapper.AvroMapper;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.hub.ScenarioAddedEvent;
import ru.practicum.collector.model.hub.enums.HubEventType;
import ru.practicum.collector.service.handler.EventProducer;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    private final AvroMapper mapper;

    public ScenarioAddedEventHandler(EventProducer eventProducer, AvroMapper mapper) {
        super(eventProducer, HubEventType.SCENARIO_ADDED);
        this.mapper = mapper;
    }


    @Override
    public void handle(HubEvent event) {
        eventProducer.sendHubEvent(mapToHubEventAvro(event));
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEvent event) {
        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) event;
        return ScenarioAddedEventAvro.newBuilder()
                .setActions(scenarioAddedEvent.getActions().stream()
                        .map(mapper::toDeviceActionAvro)
                        .toList())
                .setConditions(scenarioAddedEvent.getConditions().stream()
                        .map(mapper::toScenarioConditionAvro)
                        .toList())
                .setName(scenarioAddedEvent.getName())
                .build();
    }
}
