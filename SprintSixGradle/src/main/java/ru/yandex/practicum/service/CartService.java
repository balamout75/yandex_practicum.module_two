package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.CartDto;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapper.ActionModes;
import ru.yandex.practicum.mapper.ItemToDtoMapper;
//import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.CartItemRepository;
//import ru.yandex.practicum.repository.CartItemRepository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CartService {

    @Value("${images.path}")
    private String UPLOAD_DIR;

    private final CartItemRepository repository;

    public CartService(CartItemRepository repository) {
            this.repository = repository;
    }

    public Flux<ItemDto> getCart(long userId) {
        return repository.inCartItems(userId).map(u -> ItemToDtoMapper.toDto(u, UPLOAD_DIR));
    }

    public Mono<Long> getCartCount(long userId) {
        return repository.inCartCount(userId);
    }

    public Mono<Object> changeInCardCount(long userId, long itemId, ActionModes action) {
        return switch (action) {
            case ActionModes.PLUS   -> repository.findByUserIdAndItemId(userId, itemId)
                .defaultIfEmpty(new CartItem(userId, itemId))
                .flatMap(u -> repository.save(u.countPlus()).then());
            case ActionModes.MINUS  -> repository.findByUserIdAndItemId(userId, itemId)
                .switchIfEmpty(Mono.empty())
                .flatMap(u -> (u.getCount()>1) ? repository.save(u.countMinus()) : repository.delete(u));
            case ActionModes.DELETE  -> repository.findByUserIdAndItemId(userId, itemId)
                .switchIfEmpty(Mono.empty())
                .flatMap(repository::delete);
            case ActionModes.NOTHING  -> Mono.empty();
        };

    }

}