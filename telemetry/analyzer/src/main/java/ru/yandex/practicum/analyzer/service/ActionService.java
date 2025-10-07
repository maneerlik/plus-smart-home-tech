package ru.yandex.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.analyzer.model.Action;
import ru.yandex.practicum.analyzer.repository.ActionRepository;

@Service
@RequiredArgsConstructor
public class ActionService {
    private final ActionRepository repository;


    public Action saveAction(Action action) {
        return repository.save(action);
    }
}
