package ru.yandex.practicum.mapping;

import ru.yandex.practicum.model.InCart;
import ru.yandex.practicum.model.InOrder;
import ru.yandex.practicum.model.Order;

public class InCartToInOrderMapper {
    private Order order;

    public InCartToInOrderMapper() {     }

    public InOrder toInOrder(InCart  inCart) {
        InOrder inOrder = new InOrder();
        inOrder.setOrder(order);
        inOrder.setItem(inCart.getItem());
        inOrder.setCount(inCart.getCount());
        return inOrder;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}
