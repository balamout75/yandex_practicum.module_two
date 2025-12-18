package ru.yandex.practicum.service.shoping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.shoping.ItemDto;
import ru.yandex.practicum.mapper.ActionModes;
import ru.yandex.practicum.mapper.ItemToDtoMapper;
import ru.yandex.practicum.model.shoping.CartItem;
import ru.yandex.practicum.model.shoping.CartItemId;
import ru.yandex.practicum.repository.CartItemRepository;

@Service
public class CartItemService {

    private final ItemService itemService;
    private final CartItemRepository repository;
    private final UserCacheVersionService userCacheVersionService;

    @Value("${images.path}")
    private String UPLOAD_DIR;
    final String KEY = "page";

    public CartItemService(ItemService itemService, CartItemRepository repository, UserCacheVersionService userCacheVersionService) {
        this.itemService = itemService;
        this.repository = repository;
        this.userCacheVersionService = userCacheVersionService;
    }

    public Flux<ItemDto> getCart(long userId) {
        return repository.findByUserId(userId)
                .flatMap(cartItem -> itemService.findItemById(cartItem.getItemId())
                        .map(item -> ItemToDtoMapper.toDto(item, cartItem.getCount(), UPLOAD_DIR)));
    }

    public Mono<Long> getCartCount(long userId) {
        return repository.inCartCount(userId);
    }

    @CacheEvict(
            value = "item",
            key = "#userId + '-' + #itemId"
    )
    public Mono<Void> changeInCardCount(long userId, long itemId, ActionModes action) {
        return switch (action) {
            case ActionModes.PLUS ->
                    repository.findByUserIdAndItemId(userId, itemId)
                            .defaultIfEmpty(new CartItem(userId, itemId))
                            .flatMap(u -> repository.save(u.countPlus()))
                            .flatMap(c -> userCacheVersionService.increment(userId))
                            .then(Mono.empty());

            case ActionModes.MINUS ->
                    repository.findByUserIdAndItemId(userId, itemId)
                            .flatMap(d ->  userCacheVersionService.increment(userId).thenReturn(d))
                            .flatMap(u -> (u.getCount() > 1) ? repository.save(u.countMinus()) : repository.delete(u))
                            .then(Mono.empty());
            case ActionModes.DELETE ->
                    repository.findByUserIdAndItemId(userId, itemId)
                            .flatMap(d ->  userCacheVersionService.increment(userId).thenReturn(d))
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

    public Mono<Void> deleteByUserId(Long userId) { return repository.deleteByUserId(userId); }
}