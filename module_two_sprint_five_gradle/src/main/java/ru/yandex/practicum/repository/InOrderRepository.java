package ru.yandex.practicum.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.OrderItem;


@Repository
public interface InOrderRepository extends JpaRepository<OrderItem, Integer> {

}