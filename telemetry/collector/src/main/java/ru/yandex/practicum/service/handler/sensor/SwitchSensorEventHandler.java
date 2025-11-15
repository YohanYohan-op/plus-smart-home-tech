package ru.yandex.practicum.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.model.enums.SensorEventType;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;
import ru.yandex.practicum.service.KafkaEventProducer;


@Component
public class SwitchSensorEventHandler extends BaseSensorEventHandler<SwitchSensorAvro> {
    public SwitchSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

    @Override
    protected SwitchSensorAvro mapToAvro(SensorEvent event) {
        SwitchSensorEvent _event = (SwitchSensorEvent) event;

        return SwitchSensorAvro.newBuilder()
                .setState(_event.isState())
                .build();
    }

}
