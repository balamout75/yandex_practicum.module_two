package ru.yandex.practicum.mapping;

import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.OrderItem;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderItemId;

public class FromCartToOrderMapper {
    //private Order order;

    public FromCartToOrderMapper() {     }

    public OrderItem toInOrder(Order order, CartItem cartItem) {
        return new OrderItem(order,cartItem.getItem(),cartItem.getCount());
    }


}
