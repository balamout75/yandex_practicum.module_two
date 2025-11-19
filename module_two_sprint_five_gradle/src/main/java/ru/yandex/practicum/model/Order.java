package ru.yandex.practicum.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @OneToMany
    @JoinTable(name = "in_order",
            joinColumns =
                    { @JoinColumn(name = "order_id", referencedColumnName = "id") },
            inverseJoinColumns =
                    { @JoinColumn(name = "item_id", referencedColumnName = "id") })
    private Set<Item> items;// = new LinkedHashSet<>();

    @ManyToOne
    @JoinColumn(name="user.id", nullable=false)
    private User user;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

}