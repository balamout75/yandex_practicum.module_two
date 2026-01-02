package ru.yandex.practicum.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.shoping.User;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<User> findBySub(String sub);

    @Query("select nextval ('users_sequence')")
    Mono<Long> getId();
}
