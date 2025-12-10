package ru.yandex.practicum.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.Order;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
    @Query("select nextval ('orders_sequence')")
    Mono<Long> getId();
}
