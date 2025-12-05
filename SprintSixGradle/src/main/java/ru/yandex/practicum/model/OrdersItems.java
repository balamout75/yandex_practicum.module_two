package ru.yandex.practicum.model;

public record OrdersItems(
        long orderid,
        long itemid,
        String title,
        long price,
        long count) {
}

