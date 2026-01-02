package ru.yandex.practicum.model.shoping;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "orders")
public class Order implements Persistable<Long> {
    @Id
    @Column("id")
    private Long id;

    @Column("user_id")
    private Long userId;

    @Transient
    private boolean isNew = false;

    public Order(Long userId, Long id) {
        this.userId = userId;
        this.id = id;
        isNew=true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}