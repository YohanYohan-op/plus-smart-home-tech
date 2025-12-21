package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.analyzer.exceptions.IllegalStateException;
import ru.yandex.practicum.telemetry.analyzer.model.Action;
import ru.yandex.practicum.telemetry.analyzer.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.model.ConditionOperation;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.repository.ActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class ScenarioService {
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;

    public void save(ScenarioAddedEventAvro event, String hubId) {
        Set<String> sensors = new HashSet<>();
        event.getConditions().forEach(condition -> sensors.add(condition.getSensorId()));
        event.getActions().forEach(action -> sensors.add(action.getSensorId()));

        boolean allSensorsExists = sensorRepository.existsByIdInAndHubId(sensors, hubId);
        if (!allSensorsExists) {
            throw new IllegalStateException("Нет возможности создать сценарий с использованием неизвестного устройства");
        }

        Optional<Scenario> maybeExist = scenarioRepository.findByHubIdAndName(hubId, event.getName());

        Scenario scenario;
        if (maybeExist.isEmpty()) {
            scenario = new Scenario();
            scenario.setName(event.getName());
            scenario.setHubId(hubId);
        } else {
            scenario = maybeExist.get();
            Map<String, Condition> conditions = scenario.getConditions();
            conditionRepository.deleteAll(conditions.values());
            scenario.getConditions().clear();

            Map<String, Action> actions = scenario.getActions();
            actionRepository.deleteAll(actions.values());
            scenario.getActions().clear();
        }

        for (ScenarioConditionAvro eventCondition : event.getConditions()) {
            Condition condition = new Condition();
            condition.setType(eventCondition.getType());
            condition.setOperation(ConditionOperation.from(eventCondition.getOperation()));
            condition.setValue(mapValue(eventCondition.getValue()));

            scenario.addCondition(eventCondition.getSensorId(), condition);
        }

        for (DeviceActionAvro eventAction : event.getActions()) {
            Action action = new Action();
            action.setType(eventAction.getType());
            if (eventAction.getType().equals(ActionTypeAvro.SET_VALUE)) {
                action.setValue(mapValue(eventAction.getValue()));
            }

            scenario.addAction(eventAction.getSensorId(), action);
        }

        conditionRepository.saveAll(scenario.getConditions().values());
        actionRepository.saveAll(scenario.getActions().values());
        scenarioRepository.save(scenario);
    }

    public void delete(String name, String hubId) {
        Optional<Scenario> optScenario = scenarioRepository.findByHubIdAndName(hubId, name);
        if (optScenario.isPresent()) {
            Scenario scenario = optScenario.get();
            conditionRepository.deleteAll(scenario.getConditions().values());
            actionRepository.deleteAll(scenario.getActions().values());
            scenarioRepository.delete(scenario);
        }
    }

    private Integer mapValue(Object value) {
        if (value != null) {
            if (value instanceof Integer i) return i;
            if (value instanceof Boolean b) return b ? 1 : 0;
        }
        return null;
    }
}