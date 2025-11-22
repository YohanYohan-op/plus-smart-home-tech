package ru.yandex.practicum.telemetry.analyzer.model;

public interface Operation {
    boolean apply(Integer left, Integer right);
}
