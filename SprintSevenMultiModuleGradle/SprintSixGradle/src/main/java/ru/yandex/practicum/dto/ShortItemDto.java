package ru.yandex.practicum.dto;

public record ShortItemDto(
        long id,
        String title,
        long price,
        long count) {
}

