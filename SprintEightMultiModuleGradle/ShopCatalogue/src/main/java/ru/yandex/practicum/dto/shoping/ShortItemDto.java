package ru.yandex.practicum.dto.shoping;

public record ShortItemDto(
        long id,
        String title,
        long price,
        long count) {
}

