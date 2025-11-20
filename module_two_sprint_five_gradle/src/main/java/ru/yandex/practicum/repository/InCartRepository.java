package ru.yandex.practicum.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.InCart;


@Repository
public interface InCartRepository extends JpaRepository<InCart, Integer> {

    InCart findByItem_IdAndUser_Id(Long itemId, Long userId);
}