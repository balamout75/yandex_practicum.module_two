package ru.yandex.practicum.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.UsersItems;


@Repository
public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Integer> {
    @Query("select i.id as id, i.title as title, i.description as description, i.imgpath as imgpath, i.price as price, " +
            "CASE WHEN ci.count is NULL tHEN 0 ELSE ci.count END as count " +
            "from items i left join cart_items  ci on i.id = ci.item_id where i.id = :itemId")
    Mono<UsersItems> findById(long userId, Long itemId);
}