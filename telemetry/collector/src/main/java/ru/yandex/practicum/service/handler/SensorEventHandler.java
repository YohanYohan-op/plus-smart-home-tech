package ru.yandex.practicum.service.handler;

import ru.yandex.practicum.model.enums.SensorEventType;
import ru.yandex.practicum.model.sensor.SensorEvent;

public interface SensorEventHandler {
    SensorEventType getMessageType();

    void handle(SensorEvent event);
}
