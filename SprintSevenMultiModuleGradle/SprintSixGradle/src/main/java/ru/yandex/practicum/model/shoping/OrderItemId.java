package ru.yandex.practicum.model.shoping;

import org.springframework.data.relational.core.mapping.Column;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class OrderItemId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column("order_id")
    private Long orderId;
    @Column("item_id")
    private Long itemId;

    public OrderItemId() { }

    public OrderItemId(Long orderId, Long itemId) {
        this.orderId = orderId;
        this.itemId = itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId entity = (OrderItemId) o;
        return Objects.equals(this.orderId, entity.orderId) &&
                Objects.equals(this.itemId, entity.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, itemId);
    }
}
