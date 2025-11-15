package ru.yandex.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.model.enums.HubEventType;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.device.DeviceAddedEvent;
import ru.yandex.practicum.service.KafkaEventProducer;


@Component
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {
    public DeviceAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEvent event) {
        DeviceAddedEvent _event = (DeviceAddedEvent) event;

        return DeviceAddedEventAvro.newBuilder()
                .setId(_event.getId())
                .setType(DeviceTypeAvro.valueOf(_event.getDeviceType().name()))
                .build();
    }
}
