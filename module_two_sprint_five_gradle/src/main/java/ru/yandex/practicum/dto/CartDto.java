package ru.yandex.practicum.dto;

import java.util.List;

public record CartDto(
        List<ItemDto> items,
        long total) {
}