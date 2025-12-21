package ru.yandex.practicum.service.handler;

import ru.yandex.practicum.model.enums.HubEventType;
import ru.yandex.practicum.model.hub.HubEvent;

public interface HubEventHandler {
    HubEventType getMessageType();

    void handle(HubEvent event);
}