package ru.yandex.practicum.dto;

public record ShortItemDto(
        long id,
        String title,
        long price,
        long count) {

    public ShortItemDto(long id, String title, long price, long count) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.count = count;
    }
}

