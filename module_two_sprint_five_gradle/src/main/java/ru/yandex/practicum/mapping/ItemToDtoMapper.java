package ru.yandex.practicum.mapping;

import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.InCart;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.User;

public class ItemToDtoMapper {
    @Value("${images.path}")
    private String UPLOAD_DIR;

    public ItemToDtoMapper() {   }

    public ItemDto toDto(User user, Item item) {
        long count = item.getInCards().stream()
                            .filter(inCard -> inCard.getUser().equals(user))
                            .map(InCart::getCount)
                            .reduce(0L, Long::sum);

        return new ItemDto(item.getId(),
                           item.getTitle(),
                           item.getDescription(),
                           UPLOAD_DIR+item.getImgPath(),
                           item.getPrice(),
                           count);
    }
}
