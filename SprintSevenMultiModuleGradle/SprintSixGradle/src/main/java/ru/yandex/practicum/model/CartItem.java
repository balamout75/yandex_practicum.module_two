package ru.yandex.practicum.model;

import jakarta.annotation.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "cart_items")
public class CartItem implements Persistable<CartItemId> {

    @Id
    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    private CartItemId id;

    @Column("user_id")
    private Long userId;

    @Column("item_id")
    private Long itemId;

    @Column("count")
    private Long count;

    @Transient
    private boolean isNew = false;

    public CartItem() {}

    public CartItem(Long userId, Long itemId) {
        this.userId = userId;
        this.itemId = itemId;
        this.count = 0L;
        this.isNew = true;
        id = new CartItemId(userId, itemId);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public CartItem countPlus() {
        this.count++;
        return this;
    }
    public CartItem countMinus() {
        this.count--;
        return this;
    }

    @Override
    public @Nullable CartItemId getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}