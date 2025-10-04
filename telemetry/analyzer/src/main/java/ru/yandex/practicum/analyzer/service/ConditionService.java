package ru.yandex.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.analyzer.model.Condition;
import ru.yandex.practicum.analyzer.repository.ConditionRepository;

@Service
@RequiredArgsConstructor
public class ConditionService {
    private final ConditionRepository repository;


    public Condition saveCondition(Condition condition) {
        return repository.save(condition);
    }
}
