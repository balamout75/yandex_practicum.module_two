package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.model.OrdersItems;
import ru.yandex.practicum.model.UsersItems;


public class OrderToDtoMapper {


    public OrderToDtoMapper() {   }

    public static ItemDto toDto(OrdersItems ordersItems, String uploadDir) {
        return new ItemDto(ordersItems.id(),
                ordersItems.title(),
                ordersItems.description(),
                uploadDir.concat(ordersItems.imgpath()),
                ordersItems.price(),
                ordersItems.count());
    }
}
