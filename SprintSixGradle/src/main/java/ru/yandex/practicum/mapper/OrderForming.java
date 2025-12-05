package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ShortItemDto;
import ru.yandex.practicum.model.OrdersItems;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class OrderForming {
    public static OrderDto toOrderDto(List<OrdersItems> ordersItemsList) {
        if (ordersItemsList.isEmpty()) return null;
        long orderId = ordersItemsList.getFirst().orderid();
        AtomicLong totalSum = new AtomicLong();
        List <ShortItemDto> list = ordersItemsList.stream()
                        .map(ItemToDtoMapper::toShortDto)
                        .peek(u -> totalSum.set(totalSum.get() + (u.count() * u.price())))
                        .toList();
        return new OrderDto(orderId, list, totalSum.get());
    }
}
