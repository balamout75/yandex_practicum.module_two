package ru.yandex.practicum.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.yandex.practicum.model.User;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
}
