package ru.yandex.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.UsersItems;

public interface ItemRepository extends ReactiveCrudRepository<Item, Long> {
    @Query("select i.id as id, i.title as title, i.description as description, i.imgpath as imgpath, i.price as price, " +
            "CASE WHEN ci.count is NULL tHEN 0 ELSE ci.count END as count " +
            "from items i left join cart_items  ci on i.id = ci.item_id")
    Flux<UsersItems> findAll(Pageable pageable);

    @Query("select i.id as id, i.title as title, i.description as description, i.imgpath as imgpath, i.price as price, " +
            "CASE WHEN ci.count is NULL tHEN 0 ELSE ci.count END as count " +
            "from items i left join cart_items  ci on i.id = ci.item_id where i.id = :itemId")
    Mono<UsersItems> findById(long userId, Long itemId);
}
