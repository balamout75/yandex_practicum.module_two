package ru.yandex.practicum.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.model.*;


@Repository
public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, OrderItemId> {
    @Query("select      oi.order_id as orderid," +
                        "i.id as itemid, " +
                        "i.title as title, " +
                        "i.price as price, " +
                        "oi.count as count " +
            "from items i join order_items oi on i.id = oi.item_id")
    Flux<OrdersItems> findByUser();

    @Query("select      oi.order_id as orderid, " +
            "i.id       as itemid, " +
            "i.title    as title, " +
            "i.price    as price, " +
            "oi.count   as count " +
            "from items i join order_items oi on i.id = oi.item_id " +
            "where oi.order_id  = :orderId")
    Flux<OrdersItems> findByUserAndOrder(Long orderId);



    @Query("select * from order_items oi join orders o " +
                    "on oi.order_id = o.id where o.user_id=:userId")
    Flux<OrderItem> findByUser (Long userId);

    @Query("select * from order_items oi where oi.order_id=:orderId")
    Flux<OrderItem> findByOrder (Long orderId);



}