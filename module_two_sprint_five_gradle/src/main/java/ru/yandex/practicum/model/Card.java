package ru.yandex.practicum.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @OneToMany
    @JoinTable(name = "in_card",
            joinColumns =
                    { @JoinColumn(name = "card_id", referencedColumnName = "id") },
            inverseJoinColumns =
                    { @JoinColumn(name = "item_id", referencedColumnName = "id") })
    private Set<Item> items;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}