package ru.yandex.practicum.collector.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import static java.util.Objects.isNull;

@Component
public class AvroMapper {
    public DeviceActionAvro toDeviceActionAvro(DeviceActionProto deviceAction) {
        if (isNull(deviceAction)) return null;

        return DeviceActionAvro.newBuilder()
                .setType(ActionTypeAvro.valueOf(deviceAction.getType().name()))
                .setSensorId(deviceAction.getSensorId())
                .setValue(deviceAction.getValue())
                .build();
    }

    public ScenarioConditionAvro toScenarioConditionAvro(ScenarioConditionProto scenarioCondition) {
        if (isNull(scenarioCondition)) return null;

        return ScenarioConditionAvro.newBuilder()
                .setType(ConditionTypeAvro.valueOf(scenarioCondition.getType().name()))
                .setValue(
                    switch (scenarioCondition.getValueCase()) {
                        case BOOL_VALUE -> scenarioCondition.getBoolValue();
                        case INT_VALUE -> scenarioCondition.getIntValue();
                        case VALUE_NOT_SET -> null;
                    }
                )
                .setOperation(ConditionOperationAvro.valueOf(scenarioCondition.getOperation().name()))
                .setSensorId(scenarioCondition.getSensorId())
                .build();
    }
}
