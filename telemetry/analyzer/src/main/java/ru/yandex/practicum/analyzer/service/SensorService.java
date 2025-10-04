package ru.yandex.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.analyzer.model.Sensor;
import ru.yandex.practicum.analyzer.repository.SensorRepository;

@Service
@RequiredArgsConstructor
public class SensorService {
    private final SensorRepository repository;


    public void saveSensor(Sensor sensor) {
        String id = sensor.getId();
        String hubId = sensor.getHubId();

        boolean exists = repository.existsByIdAndHubId(id, hubId);

        if (!exists) repository.save(sensor);
    }

    public void removeSensor(String id) {
        repository.deleteById(id);
    }
}
