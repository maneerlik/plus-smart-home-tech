package ru.yandex.practicum.analyzer.mapper.entity;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.model.Action;
import ru.yandex.practicum.analyzer.model.Condition;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.model.enumeration.ActionType;
import ru.yandex.practicum.analyzer.model.enumeration.ConditionOperation;
import ru.yandex.practicum.analyzer.model.enumeration.ConditionType;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ScenarioMapper {
    public static Scenario toScenario(String hubId, ScenarioAddedEventAvro avro) {
        Scenario scenario = new Scenario();

        scenario.setHubId(hubId);
        scenario.setName(avro.getName());

        Map<String, Condition> conditionMap = avro.getConditions().stream()
                .map(ScenarioMapper::toCondition)
                .collect(Collectors.toMap(Condition::getSensorId, Function.identity()));

        Map<String, Action> actionMap = avro.getActions().stream()
                .map(ScenarioMapper::toAction)
                .collect(Collectors.toMap(Action::getSensorId, Function.identity()));

        scenario.setConditions(conditionMap);
        scenario.setActions(actionMap);

        return scenario;
    }

    private static Action toAction(DeviceActionAvro avro) {
        Action action = new Action();

        action.setSensorId(avro.getSensorId());
        action.setType(ActionType.valueOf(avro.getType().name()));
        action.setValue(avro.getValue() != null ? avro.getValue() : 0);

        return action;
    }

    private static Condition toCondition(ScenarioConditionAvro avro) {
        Condition condition = new Condition();

        condition.setSensorId(avro.getSensorId());
        condition.setType(ConditionType.valueOf(avro.getType().name()));
        condition.setOperation(ConditionOperation.valueOf(avro.getOperation().name()));
        condition.setValue(extractConditionValue(avro.getValue()));

        return condition;
    }

    private static Integer extractConditionValue(Object value) {
        if (value instanceof Integer i) return i;
        if (value instanceof Boolean b) return b ? 1 : 0;
        return null;
    }
}
