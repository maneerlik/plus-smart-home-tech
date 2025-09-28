package ru.yandex.practicum.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.storage.SensorSnapshotStorage;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemorySensorSnapshotStorage implements SensorSnapshotStorage {
    private final Map<String, SensorsSnapshotAvro> snapshots = new ConcurrentHashMap<>();


    @Override
    public Optional<SensorsSnapshotAvro> updateSnapshot(SensorEventAvro event) {
        String hubId = event.getHubId();
        String sensorId = event.getId();
        Instant eventTimestamp = event.getTimestamp();

        SensorsSnapshotAvro snapshot = snapshots.get(hubId);

        if (Objects.isNull(snapshot)) {
            snapshot = SensorsSnapshotAvro.newBuilder()
                    .setHubId(hubId)
                    .setTimestamp(Instant.now())
                    .setSensorsState(new HashMap<>())
                    .build();

            snapshots.put(hubId, snapshot);
        }

        Map<String, SensorStateAvro> stateMap = snapshot.getSensorsState();
        SensorStateAvro oldState = stateMap.get(sensorId);

        if (Objects.nonNull(oldState)) {
            boolean isAfterState = eventTimestamp.isAfter(oldState.getTimestamp());
            boolean isEqualPayload = oldState.getData().equals(event.getPayload());

            if (isAfterState || isEqualPayload) return Optional.empty();
        }

        SensorStateAvro newState = new SensorStateAvro();
        newState.setTimestamp(eventTimestamp);
        newState.setData(event.getPayload());

        stateMap.put(sensorId, newState);
        snapshot.setTimestamp(eventTimestamp);

        return Optional.of(snapshot);
    }

    @Override
    public Map<String, SensorsSnapshotAvro> getSnapshots() {
        return snapshots;
    }
}
