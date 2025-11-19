package ru.yandex.practicum.mapping;

import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.Item;

public class ItemEntityMapper {
    @Value("${images.path}")
    private String UPLOAD_DIR;

    public ItemEntityMapper() {   }

    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(),
                           item.getTitle(),
                           item.getDescription(),
                           UPLOAD_DIR+item.getImgPath(),
                           item.getPrice(),
                           item.getQuantity());
    }
}
