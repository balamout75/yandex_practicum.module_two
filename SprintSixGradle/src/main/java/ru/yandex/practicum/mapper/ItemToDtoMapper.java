package ru.yandex.practicum.mapper;

import reactor.util.function.Tuple2;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.dto.ShortItemDto;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.OrderItem;


public class ItemToDtoMapper {


    public ItemToDtoMapper() {   }


    public static ItemDto toDto(Item item, Long count, String uploadDir) {
        return new ItemDto(item.getId(),
                item.getTitle(),
                item.getDescription(),
                uploadDir.concat(item.getImgPath()),
                item.getPrice(),
                count);
    }

    public static ShortItemDto toShortDto2(Tuple2<OrderItem, Item> tuple) {
        return new ShortItemDto(tuple.getT2().getId(),
                tuple.getT2().getTitle(),
                tuple.getT2().getPrice(),
                tuple.getT1().getCount());
    }
}
