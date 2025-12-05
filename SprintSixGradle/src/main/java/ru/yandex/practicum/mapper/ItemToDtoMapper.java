package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.dto.ShortItemDto;
import ru.yandex.practicum.model.OrdersItems;
import ru.yandex.practicum.model.UsersItems;


public class ItemToDtoMapper {


    public ItemToDtoMapper() {   }

    public static ItemDto toDto(UsersItems usersItems, String uploadDir) {
        return new ItemDto(usersItems.id(),
                usersItems.title(),
                usersItems.description(),
                uploadDir.concat(usersItems.imgpath()),
                usersItems.price(),
                usersItems.count());
    }
    public static ShortItemDto toShortDto(OrdersItems ordersItems) {
        return new ShortItemDto(ordersItems.itemid(),
                ordersItems.title(),
                ordersItems.price(),
                ordersItems.count());
    }
}
