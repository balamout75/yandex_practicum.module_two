package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.User;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository <Order, Long> {

    List<Order> findByUser(User user);
    //
}
