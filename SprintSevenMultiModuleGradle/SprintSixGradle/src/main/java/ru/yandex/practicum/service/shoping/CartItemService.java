package ru.yandex.practicum.service.shoping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.shoping.ItemDto;
import ru.yandex.practicum.dto.shoping.PageDto;
import ru.yandex.practicum.mapper.ActionModes;
import ru.yandex.practicum.mapper.ItemToDtoMapper;
import ru.yandex.practicum.model.shoping.CartItem;
import ru.yandex.practicum.model.shoping.CartItemId;
import ru.yandex.practicum.repository.CartItemRepository;

@Service
public class CartItemService {

    private final ItemService itemService;
    private final CartItemRepository repository;
    @Value("${images.path}")
    private String UPLOAD_DIR;

    @Autowired
    private ReactiveHashOperations<String, Integer, PageDto> pageDtoHashOperations;

    public CartItemService(ItemService itemService, CartItemRepository repository) {
        this.itemService = itemService;
        this.repository = repository;
    }

    public Flux<ItemDto> getCart(long userId) {
        return repository.findByUserId(userId)
                .flatMap(cartItem -> Mono.just(cartItem).zipWhen(ci -> itemService.findItemById(ci.getItemId())))
                .map(u -> ItemToDtoMapper.toDto(u.getT2(), u.getT1().getCount(), UPLOAD_DIR));
    }

    public Mono<Long> getCartCount(long userId) {
        return repository.inCartCount(userId);
    }

    @CacheEvict(
            value = "item",
            key   = "#userId.toString().concat('-').concat(#itemId.toString)"
    )
    public Mono<Void> changeInCardCount(long userId, long itemId, ActionModes action) {
        final String KEY = "page"+userId;
        return switch (action) {
            case ActionModes.PLUS ->
                    repository.findByUserIdAndItemId(userId, itemId)
                            .defaultIfEmpty(new CartItem(userId, itemId))
                            .flatMap(u -> repository.save(u.countPlus()))
                            .flatMap(c -> pageDtoHashOperations.delete(KEY))
                            .then(Mono.empty());

            case ActionModes.MINUS ->
                    repository.findByUserIdAndItemId(userId, itemId)
                            .switchIfEmpty(Mono.empty())
                            .flatMap(d -> pageDtoHashOperations.delete(KEY).thenReturn(d))
                            .flatMap(u -> (u.getCount() > 1) ? repository.save(u.countMinus()) : repository.delete(u))
                            .then(Mono.empty());
            case ActionModes.DELETE ->
                    repository.findByUserIdAndItemId(userId, itemId)
                            .switchIfEmpty(Mono.empty())
                            .flatMap(d -> pageDtoHashOperations.delete(KEY).thenReturn(d))
                            .flatMap(repository::delete)
                            .then(Mono.empty());
            case ActionModes.NOTHING -> Mono.empty();
        };

    }

    public Mono<Long> getInCartCount(long userId, long itemId) {
        return repository.findByUserIdAndItemId(userId, itemId)
                .map(CartItem::getCount).switchIfEmpty(Mono.just(0L));
    }

    public Flux<CartItem> findByUserId(long userId) {
        return repository.findByUserId(userId);
    }

    public Mono<Void> deleteById(CartItemId cartItemId) {
        return repository.deleteById(cartItemId);
    }
}