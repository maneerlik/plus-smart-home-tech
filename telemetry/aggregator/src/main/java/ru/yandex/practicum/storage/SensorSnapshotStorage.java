package ru.yandex.practicum.storage;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Map;
import java.util.Optional;

public interface SensorSnapshotStorage {
    Optional<SensorsSnapshotAvro> updateSnapshot(SensorEventAvro event);

    Map<String, SensorsSnapshotAvro> getSnapshots();
}
