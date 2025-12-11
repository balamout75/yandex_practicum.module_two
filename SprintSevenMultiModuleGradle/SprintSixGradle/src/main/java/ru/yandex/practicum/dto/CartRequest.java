package ru.yandex.practicum.dto;

import ru.yandex.practicum.mapper.ActionModes;

public record CartRequest(
        long id,
        ActionModes action) {
}