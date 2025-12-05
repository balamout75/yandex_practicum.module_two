package ru.yandex.practicum.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.UsersItems;

import java.util.List;


@Repository
public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Long> {
    @Query("select i.id as id, i.title as title, i.description as description, i.imgpath as imgpath, i.price as price, " +
            "CASE WHEN ci.count is NULL tHEN 0 ELSE ci.count END as count " +
            "from items i join cart_items ci on i.id = ci.item_id where ci.user_id  = :userId")
    Flux<UsersItems> inCartItems(Long userId);

    @Query("select SUM(i.price*ci.count) from items i join cart_items ci on i.id = ci.item_id where ci.user_id  = :userId")
    Mono<Long> inCartCount(Long userId);

    Mono<CartItem> findByUserIdAndItemId(long userId, long itemId);

    //Mono<Long> updateCountByUserIdAndItemId(long count,long userId, long itemId);
}