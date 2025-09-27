package ru.practicum.collector.service.handler.hub.impl;

import org.springframework.stereotype.Component;
import ru.practicum.collector.service.handler.EventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {
    public ScenarioRemovedEventHandler(EventProducer eventProducer) {
        super(eventProducer, HubEventProto.PayloadCase.SCENARIO_REMOVED);
    }


    @Override
    public void handle(HubEventProto event) {
        eventProducer.sendHubEvent(mapToHubEventAvro(event));
    }

    @Override
    protected ScenarioRemovedEventAvro mapToAvro(HubEventProto event) {
        ScenarioRemovedEventProto scenarioRemovedEvent = event.getScenarioRemoved();
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(scenarioRemovedEvent.getName())
                .build();
    }
}
