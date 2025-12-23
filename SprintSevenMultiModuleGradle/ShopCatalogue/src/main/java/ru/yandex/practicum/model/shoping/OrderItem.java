package ru.yandex.practicum.model.shoping;

import jakarta.annotation.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "order_items")
public class OrderItem implements Persistable<OrderItemId> {

    @Id
    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    private OrderItemId id;

    @Column("order_id")
    private Long orderId;

    @Column("item_id")
    private Long itemId;

    @Column("count")
    private Long count;

    @Transient
    private boolean isNew = false;

    public OrderItem() {}

    public OrderItem(Long orderId, Long itemId, Long count) {
        this.orderId    = orderId;
        this.itemId     = itemId;
        this.count      = count;
        this.isNew      = true;
        id = new OrderItemId(orderId, itemId);
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public OrderItem countPlus() {
        this.count++;
        return this;
    }
    public OrderItem countMinus() {
        this.count--;
        return this;
    }

    @Override
    public @Nullable OrderItemId getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}