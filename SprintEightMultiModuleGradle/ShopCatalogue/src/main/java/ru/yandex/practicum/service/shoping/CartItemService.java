package ru.yandex.practicum.service.shoping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.shoping.ItemDto;
import ru.yandex.practicum.mapper.ActionModes;
import ru.yandex.practicum.mapper.ItemToDtoMapper;
import ru.yandex.practicum.model.shoping.CartItem;
import ru.yandex.practicum.repository.CartItemRepository;
import ru.yandex.practicum.security.CurrentUserFacade;

import java.util.Comparator;

@Service
public class CartItemService {

    private static final Logger log = LoggerFactory.getLogger(CartItemService.class);
    private final ItemService itemService;
    private final CartItemRepository repository;
    private final UserCacheVersionService userCacheVersionService;
    private final ReactiveRedisTemplate<String, ItemDto> itemRedisTemplate;
    private final CurrentUserFacade currentUserFacade;

    @Value("${images.path}")
    private String UPLOAD_DIR;

    public CartItemService(ItemService itemService,
                           CartItemRepository repository,
                           UserCacheVersionService userCacheVersionService, ReactiveRedisTemplate<String, ItemDto> itemRedisTemplate,
                           CurrentUserFacade currentUserFacade) {
        this.itemService = itemService;
        this.repository = repository;
        this.userCacheVersionService = userCacheVersionService;
        this.itemRedisTemplate = itemRedisTemplate;
        this.currentUserFacade = currentUserFacade;
    }

    //getCart - список товаров в корзине аторизованного пользователя
    //getCartForUser - внутренний метод для известного пользователя
    public Flux<ItemDto> getInCartItems() {
        return currentUserFacade.getUserId()
                .flatMapMany(this::getInCartItemsForUser);
    }
    Flux<ItemDto> getInCartItemsForUser(long userId) {
        return repository.findByUserId(userId)
                .flatMap(cartItem -> itemService.findItemById(cartItem.getItemId())
                        .map(item -> ItemToDtoMapper.toDto(item, cartItem.getCount(), UPLOAD_DIR)))
                .sort(Comparator.comparing(ItemDto::id)); /// надоели прыгающие картинки в корзине
    }

    //Стоимость корзины
    public Mono<Long> getCartTotal() {
        return currentUserFacade.getUserId()
                .flatMap(repository::inCartTotal);
    }

    //changeInCardCount - ручка для контроллера
    //changeInCardCountForUser внутренний метод для пользователя
    //evictItemCache обнуляем кэш карточки товара для пользователя
    public Mono<Void> changeInCardCount(long itemId, ActionModes action) {
        return currentUserFacade.getUserId()
                .flatMap(userId -> changeInCardCountForUser(userId,itemId, action));
    }

    Mono<Void> changeInCardCountForUser(long userId, long itemId, ActionModes action) {
        log.info("В корзине "+userId + " изменилось состояние товара " + itemId + '-' + action.toString());
        return switch (action) {
            case ActionModes.PLUS ->
                    repository.findByUserIdAndItemId(userId, itemId)
                            .defaultIfEmpty(new CartItem(userId, itemId))
                            .flatMap(cardItem -> repository.save(cardItem.countPlus()))
                            .flatMap(cardItem -> userCacheVersionService.increment())
                            .flatMap(version -> evictItemCache(userId, itemId).then());
            case ActionModes.MINUS ->
                    repository.findByUserIdAndItemId(userId, itemId)
                            .flatMap(cardItem ->  userCacheVersionService.increment().thenReturn(cardItem))
                            .flatMap(cardItem -> evictItemCache(userId, itemId).thenReturn(cardItem))
                            .flatMap(c -> (c.getCount() > 1) ? repository.save(c.countMinus()) : repository.delete(c))
                            .then();
            case ActionModes.DELETE ->
                    repository.findByUserIdAndItemId(userId, itemId)
                            .flatMap(cardItem ->  userCacheVersionService.increment().thenReturn(cardItem))
                            .flatMap(cardItem -> evictItemCache(userId, itemId).thenReturn(cardItem))
                            .flatMap(repository::delete)
                            .then();
            case ActionModes.NOTHING -> Mono.empty();
        };

    }

    private Mono<Void> evictItemCache(long userId, long itemId) {
        String redisKey = "item:" + userId;
        return itemRedisTemplate.opsForHash()
                .remove(redisKey, String.valueOf(itemId))
                .then();
    }

    //Количество товаров в корзине  - сервисный метод для CatalogueService
    Mono<Long> getInCartCount(long userId, long itemId) {
        return repository.findByUserIdAndItemId(userId, itemId)
                .map(CartItem::getCount).switchIfEmpty(Mono.just(0L));
    }


    //findByUserId и deleteByUserId - сервисные методы для OrderService
    Flux<CartItem> findForUser(long userId) {
        return repository.findByUserId(userId);
    }

    Mono<Void> deleteForUser(Long userId) { return repository.deleteByUserId(userId); }
}