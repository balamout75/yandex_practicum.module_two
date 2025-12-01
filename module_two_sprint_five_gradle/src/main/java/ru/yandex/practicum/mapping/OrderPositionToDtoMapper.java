package ru.yandex.practicum.mapping;
import ru.yandex.practicum.dto.ShortItemDto;
import ru.yandex.practicum.model.OrderItem;

public class OrderPositionToDtoMapper {

    public OrderPositionToDtoMapper() {   }

    public ShortItemDto toDto(OrderItem orderItem) {
        return new ShortItemDto(orderItem.getItem().getId(),
                                    orderItem.getItem().getTitle(),
                                    orderItem.getItem().getPrice(),
                                    orderItem.getCount());
    }
}
