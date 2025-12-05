package ru.yandex.practicum.dto;

import ru.yandex.practicum.mapper.ActionModes;

public record ItemsRequest(
        long id,
        String search,
        String sort,
        int pageNumber,
        int pageSize,
        ActionModes action) {
}