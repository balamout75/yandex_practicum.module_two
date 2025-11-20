package ru.yandex.practicum.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "in_order")
public class InOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    //@MapsId("userId")
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    //@MapsId("itemId")
    @JoinColumn(name = "item_id")
    private Item item;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "count", nullable = false)
    private Long count;

    public InOrder() {
    }

    public InOrder(InCart inCart) {
        this.item = inCart.getItem();
        this.count = inCart.getCount();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

}