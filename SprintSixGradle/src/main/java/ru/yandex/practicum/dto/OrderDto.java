package ru.yandex.practicum.dto;

import java.util.List;

public record OrderDto (
        long id,
        List<ShortItemDto> items,
        long totalSum)
{
    public OrderDto(long id, List<ShortItemDto> items, long totalSum) {
        this.id = id;
        this.items = items;
        this.totalSum = totalSum;
    }
}
