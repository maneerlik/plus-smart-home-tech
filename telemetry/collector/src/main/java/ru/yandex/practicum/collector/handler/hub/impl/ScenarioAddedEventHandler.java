package ru.practicum.collector.service.handler.hub.impl;

import org.springframework.stereotype.Component;
import ru.practicum.collector.mapper.AvroMapper;
import ru.practicum.collector.service.handler.EventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    private final AvroMapper mapper;

    public ScenarioAddedEventHandler(EventProducer eventProducer, AvroMapper mapper) {
        super(eventProducer, HubEventProto.PayloadCase.SCENARIO_ADDED);
        this.mapper = mapper;
    }


    @Override
    public void handle(HubEventProto event) {
        eventProducer.sendHubEvent(mapToHubEventAvro(event));
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEventProto event) {
        ScenarioAddedEventProto scenarioAddedEvent = event.getScenarioAdded();
        return ScenarioAddedEventAvro.newBuilder()
                .setActions(scenarioAddedEvent.getActionList().stream()
                        .map(mapper::toDeviceActionAvro)
                        .toList())
                .setConditions(scenarioAddedEvent.getConditionList().stream()
                        .map(mapper::toScenarioConditionAvro)
                        .toList())
                .setName(scenarioAddedEvent.getName())
                .build();
    }
}
