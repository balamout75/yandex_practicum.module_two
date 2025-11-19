package ru.yandex.practicum.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public record ItemDto (
        long id,
        String title,
        String description,
        String imgPath,
        long price,
        long quantity,
        long count) {

    public ItemDto(long id, String title, String description, String imgPath, long price, long quantity, long count) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imgPath = imgPath;
        this.price = price;
        this.quantity = quantity;
        this.count = count;
    }
    public ItemDto() {
        this(-1, null, null, null, 0,0, 0);
    }
}

