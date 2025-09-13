package ru.practicum.collector.service.handler.hub.impl;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.hub.HubEvent;
import ru.practicum.collector.model.hub.ScenarioRemovedEvent;
import ru.practicum.collector.model.hub.enums.HubEventType;
import ru.practicum.collector.service.handler.EventProducer;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {
    public ScenarioRemovedEventHandler(EventProducer eventProducer) {
        super(eventProducer, HubEventType.SCENARIO_REMOVED);
    }


    @Override
    public void handle(HubEvent event) {
        eventProducer.sendHubEvent(mapToHubEventAvro(event));
    }

    @Override
    protected ScenarioRemovedEventAvro mapToAvro(HubEvent event) {
        ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) event;
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(scenarioRemovedEvent.getName())
                .build();
    }
}
