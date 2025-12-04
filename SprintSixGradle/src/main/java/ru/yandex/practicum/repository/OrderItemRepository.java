package ru.yandex.practicum.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.OrderItem;
import ru.yandex.practicum.model.OrdersItems;
import ru.yandex.practicum.model.UsersItems;


@Repository
public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {
    @Query("select i.id as id, i.title as title, i.description as description, i.imgpath as imgpath, i.price as price, " +
            "CASE WHEN oi.count is NULL tHEN 0 ELSE oi.count END as count " +
            "from items i join order_items oi on i.id = oi.item_id where oi.order_id  = :orderId")
    Flux<OrdersItems> findByUser(Long orderId);

    @Query("select SUM(i.price*oi.count) from items i join order_items oi on i.id = oi.item_id where oi.order_id  = :orderId")
    Mono<Long> inOrderCount(Long orderId);

}