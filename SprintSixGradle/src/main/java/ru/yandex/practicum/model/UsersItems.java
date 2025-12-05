package ru.yandex.practicum.model;

public record UsersItems(
        long id,
        String title,
        String description,
        String imgpath,
        long price,
        long count) {

    public UsersItems() {
        this(-1L, null, null, null, 0L, 0L);
    }
}

