package ru.yandex.practicum.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Item;




@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    Item findById(long itemId);
}
