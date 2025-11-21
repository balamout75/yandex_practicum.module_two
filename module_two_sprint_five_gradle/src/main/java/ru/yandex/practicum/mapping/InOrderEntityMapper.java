package ru.yandex.practicum.mapping;
import ru.yandex.practicum.dto.ShortItemDto;
import ru.yandex.practicum.model.InOrder;

public class InOrderEntityMapper {

    public InOrderEntityMapper() {   }

    public ShortItemDto toDto(InOrder inOrder) {
        return new ShortItemDto(inOrder.getItem().getId(),
                                    inOrder.getItem().getTitle(),
                                    inOrder.getItem().getPrice(),
                                    inOrder.getCount());
    }
}
