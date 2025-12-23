package ru.yandex.practicum.dto;

import java.util.List;

public record OrderDto (
        long id,
        List<ShortItemDto> items,
        long totalSum) {
}
