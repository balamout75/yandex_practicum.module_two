package ru.yandex.practicum.model.shoping;

import org.springframework.data.relational.core.mapping.Column;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class CartItemId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column("user_id")
    private Long userId;
    @Column("item_id")
    private Long itemId;

    public CartItemId() { }

    public CartItemId(Long userId, Long itemId) {
        this.userId = userId;
        this.itemId = itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemId entity = (CartItemId) o;
        return Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.itemId, entity.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, itemId);
    }
}
