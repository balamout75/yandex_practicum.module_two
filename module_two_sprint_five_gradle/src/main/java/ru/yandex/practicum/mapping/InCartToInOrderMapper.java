package ru.yandex.practicum.mapping;

import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.InCart;
import ru.yandex.practicum.model.Item;

public class InCartToInOrderMapper {
    private String UPLOAD_DIR;

    public InCartToInOrderMapper() {   }

    public ItemDto toDto(Item item) {
        long count = item.getInCards().stream()
                .filter(inCard -> inCard.getUser().getId().intValue()==1)
                .findFirst()
                .map(InCart::getCount)
                .orElse(0l);

        return new ItemDto(item.getId(),
                item.getTitle(),
                item.getDescription(),
                UPLOAD_DIR+item.getImgPath(),
                item.getPrice(),
                count);
    }
}
