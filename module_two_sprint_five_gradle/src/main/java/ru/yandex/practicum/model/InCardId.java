package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class InCardId implements Serializable {
    private static final long serialVersionUID = 9155777400213383044L;
    @NotNull
    @ColumnDefault("1")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "item_id", nullable = false)
    private Long itemId;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        InCardId entity = (InCardId) o;
        return Objects.equals(this.itemId, entity.itemId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, userId);
    }

}