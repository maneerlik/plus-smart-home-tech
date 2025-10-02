package ru.yandex.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.controller.HubRouterController;
import ru.yandex.practicum.analyzer.mapper.proto.ActionRequestProtoMapper;
import ru.yandex.practicum.analyzer.model.Condition;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.model.enumeration.ConditionType;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotAnalyzer {
    private final ScenarioRepository scenarioRepository;
    private final HubRouterController hubRouterController;
    private final ActionRequestProtoMapper actionRequestProtoMapper;


    public void handleSnapshot(SensorsSnapshotAvro sensorsSnapshotAvro) {
        String hubId = sensorsSnapshotAvro.getHubId();
        Map<String, SensorStateAvro> states = sensorsSnapshotAvro.getSensorsState();
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        log.info("For snapshot hubId={}, found scenarios: {}", hubId, scenarios);
        scenarios.stream()
                .filter(scenario -> checkConditions(scenario, states))
                .forEach(this::executeActions);
    }

    private boolean checkConditions(Scenario scenario, Map<String, SensorStateAvro> states) {
        Map<String, Condition> conditions = scenario.getConditions();
        if (conditions.isEmpty()) log.info("Conditions is empty");
        if (scenario.getActions().isEmpty()) log.info("Actions is empty");

        return conditions.entrySet().stream()
                .allMatch(entry -> evaluateCondition(entry.getValue(), states.get(entry.getKey())));
    }

    private boolean evaluateCondition(Condition condition, SensorStateAvro state) {
        if (Objects.isNull(state)) return false;

        Integer sensorValue = getSensorValue(state.getData(), condition.getType());
        if (Objects.isNull(sensorValue)) return false;

        return switch (condition.getOperation()) {
            case EQUALS -> sensorValue.equals(condition.getValue());
            case LOWER_THAN -> sensorValue < condition.getValue();
            case GREATER_THAN -> sensorValue > condition.getValue();
        };
    }

    private Integer getSensorValue(Object data, ConditionType type) {
        return switch (type) {
            case TEMPERATURE -> data instanceof ClimateSensorAvro climate ? climate.getTemperatureC() : null;
            case HUMIDITY -> data instanceof ClimateSensorAvro climate ? climate.getHumidity() : null;
            case CO2LEVEL -> data instanceof ClimateSensorAvro climate ? climate.getCo2Level() : null;
            case LUMINOSITY -> data instanceof LightSensorAvro light ? light.getLuminosity() : null;
            case MOTION -> data instanceof MotionSensorAvro motion ? (motion.getMotion() ? 1 : 0) : null;
            case SWITCH -> data instanceof SwitchSensorAvro switchSensor ? (switchSensor.getState() ? 1 : 0) : null;
        };
    }

    private void executeActions(Scenario scenario) {
        log.info("Worked scenario: {}", scenario.getActions());
        List<DeviceActionRequest> requests = actionRequestProtoMapper.toActionRequest(scenario);
        requests.forEach(hubRouterController::sendAction);
    }
}
