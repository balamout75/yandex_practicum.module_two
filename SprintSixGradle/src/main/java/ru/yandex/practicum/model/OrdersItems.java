package ru.yandex.practicum.model;

public record OrdersItems(
        long orderid,
        long itemid,
        String title,
        long price,
        long count) {
    public OrdersItems(long orderid, long itemid, String title, long price, long count) {
        this.orderid = orderid;
        this.itemid = itemid;
        this.title = title;
        this.price = price;
        this.count = count;
    }
}

