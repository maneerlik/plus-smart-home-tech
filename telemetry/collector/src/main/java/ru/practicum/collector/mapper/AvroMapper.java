package ru.practicum.collector.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.collector.model.hub.DeviceAction;
import ru.practicum.collector.model.hub.ScenarioCondition;
import ru.yandex.practicum.kafka.telemetry.event.*;

import static java.util.Objects.isNull;

@Component
public class AvroMapper {
    public DeviceActionAvro toDeviceActionAvro(DeviceAction deviceAction) {
        if (isNull(deviceAction)) return null;

        return DeviceActionAvro.newBuilder()
                .setType(ActionTypeAvro.valueOf(deviceAction.getType().name()))
                .setSensorId(deviceAction.getSensorId())
                .setValue(deviceAction.getValue())
                .build();
    }

    public ScenarioConditionAvro toScenarioConditionAvro(ScenarioCondition scenarioCondition) {
        if (isNull(scenarioCondition)) return null;

        return ScenarioConditionAvro.newBuilder()
                .setType(ConditionTypeAvro.valueOf(scenarioCondition.getType().name()))
                .setValue(scenarioCondition.getValue())
                .setOperation(ConditionOperationAvro.valueOf(scenarioCondition.getOperation().name()))
                .setSensorId(scenarioCondition.getSensorId())
                .build();
    }
}
