package ru.yandex.practicum.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", sequenceName = "orders_sequence", allocationSize = 1)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, mappedBy="order")
    private Set<OrderItem> orderItem;

    @ManyToOne
    @JoinColumn(name="user.id")
    private User user;

    public Order() {    };

    public Order(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<OrderItem> getInOrder() {
        return orderItem;
    }

    public void setUser(User user) {
        this.user = user;
    }
}