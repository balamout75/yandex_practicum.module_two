package ru.yandex.practicum.dto;

import ru.yandex.practicum.model.UsersItems;

import java.util.List;

public record CartDto(
        List<UsersItems> items,
        long total) {
}