package ru.yandex.practicum.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "users_sequence", allocationSize = 1)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, mappedBy="user")
    //@OneToMany(mappedBy="user")
    private Set<CartItem> CartItems;

    @OneToMany(fetch = FetchType.EAGER, mappedBy="user")
    //@OneToMany(mappedBy="user")
    private Set<Order> orders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<CartItem> getInCarts() {
        return CartItems;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }
}