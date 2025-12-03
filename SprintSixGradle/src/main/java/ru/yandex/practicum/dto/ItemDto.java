package ru.yandex.practicum.dto;

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
}

