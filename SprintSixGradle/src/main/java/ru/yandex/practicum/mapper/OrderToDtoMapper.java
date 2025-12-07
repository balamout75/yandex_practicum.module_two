package ru.yandex.practicum.mapper;

import reactor.util.function.Tuple2;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ShortItemDto;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.OrderItem;
import ru.yandex.practicum.model.OrdersItems;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class OrderToDtoMapper {
    public static OrderDto toOrderDto(List<OrdersItems> ordersItemsList, Long orderId) {
        //long orderId = ordersItemsList.getFirst().orderid();
        AtomicLong totalSum = new AtomicLong();
        List <ShortItemDto> list = ordersItemsList.stream()
                        .map(ItemToDtoMapper::toShortDto)
                        .peek(u -> totalSum.set(totalSum.get() + (u.count() * u.price())))
                        .toList();
        return new OrderDto(orderId, list, totalSum.get());
    }

    public static OrderDto toDto2(List<Tuple2<OrderItem, Item>> tuples, Long orderId) {
        AtomicLong totalSum = new AtomicLong();
        List <ShortItemDto> list = tuples.stream()
                .map(ItemToDtoMapper::toShortDto2)
                .peek(u -> totalSum.set(totalSum.get() + (u.count() * u.price())))
                .toList();
        return new OrderDto(orderId, list, totalSum.get());
    }

    public static OrderDto toDto(List<OrderItem> value, Long key) {
        return null;
    }
}
