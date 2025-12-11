package ru.yandex.practicum.dto;

import java.util.Comparator;

public record ItemDto (
        long id,
        String title,
        String description,
        String imgPath,
        long price,
        long count) {

    public ItemDto() {
        this(-1L, null, null, null, 0L, 0L);
    }

    public ItemDto(long id, String title, String description, String imgPath, long price, long count) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imgPath = imgPath;
        this.price = price;
        this.count = count;
    }

}

