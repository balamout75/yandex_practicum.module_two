package ru.yandex.practicum.dto;

import ru.yandex.practicum.mapper.ItemToDtoMapper;
import ru.yandex.practicum.model.OrdersItems;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
