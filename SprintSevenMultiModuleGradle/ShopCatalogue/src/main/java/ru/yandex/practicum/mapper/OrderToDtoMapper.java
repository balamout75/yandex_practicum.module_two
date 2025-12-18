package ru.yandex.practicum.mapper;

import reactor.util.function.Tuple2;
import ru.yandex.practicum.dto.shoping.OrderDto;
import ru.yandex.practicum.dto.shoping.ShortItemDto;
import ru.yandex.practicum.model.shoping.Item;
import ru.yandex.practicum.model.shoping.OrderItem;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class OrderToDtoMapper {

    public static OrderDto toDto2(List<Tuple2<OrderItem, Item>> tuples, Long orderId) {
        AtomicLong totalSum = new AtomicLong();
        List <ShortItemDto> list = tuples.stream()
                .map(ItemToDtoMapper::toShortDto2)
                .peek(u -> totalSum.set(totalSum.get() + (u.count() * u.price())))
                .toList();
        return new OrderDto(orderId, list, totalSum.get());
    }
}
