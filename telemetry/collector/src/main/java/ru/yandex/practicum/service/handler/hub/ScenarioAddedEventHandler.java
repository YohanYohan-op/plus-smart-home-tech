package ru.yandex.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.enums.HubEventType;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.scenario.ScenarioAddedEvent;
import ru.yandex.practicum.service.KafkaEventProducer;

import java.util.stream.Collectors;

/**
 * Обработчик события добавления сценария
 */
@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEvent event) {
        ScenarioAddedEvent _event = (ScenarioAddedEvent) event;

        return ScenarioAddedEventAvro.newBuilder()
                .setName(_event.getName())
                .setConditions(_event.getConditions().stream()
                        .map(condition -> ScenarioConditionAvro.newBuilder()
                                .setSensorId(condition.getSensorId())
                                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                                .setValue(condition.getValue())
                                .build())
                        .collect(Collectors.toList()))
                .setActions(_event.getActions().stream()
                        .map(action -> DeviceActionAvro.newBuilder()
                                .setSensorId(action.getSensorId())
                                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                                .setValue(action.getValue())
                                .build())
                        .collect(Collectors.toList())
                )
                .build();
    }
}
