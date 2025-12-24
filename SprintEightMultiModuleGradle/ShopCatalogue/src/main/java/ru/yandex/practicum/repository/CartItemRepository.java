package ru.yandex.practicum.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.shoping.CartItem;
import ru.yandex.practicum.model.shoping.CartItemId;

@Repository
public interface CartItemRepository extends ReactiveSortingRepository<CartItem, CartItemId> {

    @Query("select SUM(i.price*ci.count) from items i join cart_items ci on i.id = ci.item_id where ci.user_id  = :userId")
    Mono<Long> inCartCount(Long userId);

    Mono<CartItem> findByUserIdAndItemId(long userId, long itemId);

    Flux<CartItem> findByUserId(long userId);

    Mono<Void> deleteByUserId(Long userId);

    Mono<CartItem> save(CartItem cartItem);

    Mono<Void> delete(CartItem cartItem);
}