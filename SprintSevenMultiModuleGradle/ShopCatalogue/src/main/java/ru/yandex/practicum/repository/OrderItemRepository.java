package ru.yandex.practicum.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.model.shoping.OrderItem;
import ru.yandex.practicum.model.shoping.OrderItemId;


@Repository
public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, OrderItemId> {

    @Query("select * from order_items oi join orders o " +
                    "on oi.order_id = o.id where o.user_id=:userId")
    Flux<OrderItem> findByUser (Long userId);

    @Query("select * from order_items oi where oi.order_id=:orderId")
    Flux<OrderItem> findByOrder (Long orderId);

}