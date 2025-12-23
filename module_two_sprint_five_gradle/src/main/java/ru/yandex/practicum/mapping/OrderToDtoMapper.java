package ru.yandex.practicum.mapping;

import ru.yandex.practicum.dto.ShortItemDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.model.Order;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class OrderToDtoMapper {

    private final OrderPositionToDtoMapper orderPositionToDtoMapper;

    public OrderToDtoMapper(OrderPositionToDtoMapper orderPositionToDtoMapper) { this.orderPositionToDtoMapper = orderPositionToDtoMapper; }

    public OrderDto toDto(Order order) {
        AtomicLong total = new AtomicLong(0);
        List<ShortItemDto> convertedItems = order.getInOrder().stream()
                                       .peek(u -> total.set(total.get() + u.getCount() * u.getItem().getPrice()))
                                       .map(orderPositionToDtoMapper::toDto)
                                       .toList();
        return new OrderDto(order.getId(), convertedItems, total.get());
    }
}
