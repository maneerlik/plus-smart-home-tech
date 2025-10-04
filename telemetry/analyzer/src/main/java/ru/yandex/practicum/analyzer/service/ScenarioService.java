package ru.yandex.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScenarioService {
    private final ScenarioRepository repository;

    private final ActionService actionService;
    private final ConditionService conditionService;


    @Transactional
    public void saveScenario(Scenario scenario) {
        String hubId = scenario.getHubId();
        String name = scenario.getName();

        Optional<Scenario> dbScenario = repository.findByHubIdAndName(hubId, name);

        if (dbScenario.isEmpty()) {
            scenario.getActions().replaceAll((key, action) -> actionService.saveAction(action));
            scenario.getConditions().replaceAll((key, condition) -> conditionService.saveCondition(condition));
            repository.save(scenario);
        }
    }

    public void removeScenario(String name) {
        repository.deleteByName(name);
    }

    public List<Scenario> getHubScenarios(String hubId) {
        return repository.findByHubId(hubId);
    }
}
