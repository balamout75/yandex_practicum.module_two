package ru.yandex.practicum.mapping;

import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.InCard;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.User;

import java.util.Set;

public class ItemEntityMapper {
    @Value("${images.path}")
    private String UPLOAD_DIR;

    public ItemEntityMapper() {   }

    public ItemDto toDto(Item item) {
        Set<InCard> inCards = item.getInCards();
        long count = item.getInCards().stream()
                            .filter(inCard -> inCard.getUser().getId().intValue()==1)
                            .findFirst()
                            .map(InCard::getCount)
                            .orElse(0l);

        return new ItemDto(item.getId(),
                           item.getTitle(),
                           item.getDescription(),
                           UPLOAD_DIR+item.getImgPath(),
                           item.getPrice(),
                           count);
    }
}
