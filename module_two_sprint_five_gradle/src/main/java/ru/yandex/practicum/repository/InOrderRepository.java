package ru.yandex.practicum.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.InOrder;


@Repository
public interface InOrderRepository extends JpaRepository<InOrder, Integer> {
    InOrder findByItem_IdAndOrder_Id(Long itemId, Long userId);
}