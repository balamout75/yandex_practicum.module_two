package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.User;

@Repository
public interface UserRepository extends JpaRepository <User, Integer> {
    User findById(Long id);
}
