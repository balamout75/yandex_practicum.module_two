package ru.yandex.practicum.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.CartItem;


@Repository
public interface InCartRepository extends JpaRepository<CartItem, Integer> {

}