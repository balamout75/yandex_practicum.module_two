package ru.yandex.practicum.service.shoping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.shoping.ItemDto;
import ru.yandex.practicum.dto.shoping.PageDto;
import ru.yandex.practicum.mapper.ItemToDtoMapper;
import ru.yandex.practicum.security.CurrentUserFacade;

import java.time.Duration;
import java.util.*;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Service
public class CatalogueService {

    private final CartItemService cartItemService;
    private final ItemService itemService;
    @Value("${images.path}")
    private String UPLOAD_DIR;

    private final ReactiveRedisTemplate<String, PageDto> pageRedisTemplate;
    private final ReactiveRedisTemplate<String, ItemDto> itemRedisTemplate;
    private final CurrentUserFacade currentUserFacade;
    private final UserCacheVersionService userCacheVersionService;

    public CatalogueService(ItemService itemService,
                            CartItemService cartItemService,
                            ReactiveRedisTemplate<String, PageDto> pageRedisTemplate,
                            ReactiveRedisTemplate<String, ItemDto> itemRedisTemplate,
                            CurrentUserFacade currentUserFacade,
                            UserCacheVersionService userCacheVersionService) {
        this.itemService = itemService;
        this.cartItemService = cartItemService;
        this.pageRedisTemplate = pageRedisTemplate;
        this.itemRedisTemplate = itemRedisTemplate;
        this.currentUserFacade = currentUserFacade;
        this.userCacheVersionService = userCacheVersionService;
    }

    //методы для работы с PageDto
    //findAll - метод для "ручки"
    //findAllForUser - внутренний метод с определенным userId
    //getCachedPageDto и putCachedPageDto - внутренняя логика кэширования
    //getUserPageKey getPageKey getComparator - сервисные методы

    private String getUserPageKey(Long userId, Long version) {
        return "page:%d:%d".formatted(userId, version);
    }

    private String getPageKey(String search, Pageable pageable) {
        return "%s:%s".formatted(
                search.toLowerCase(),
                pageable.toString()
        );
    }

    private Comparator<ItemDto> getComparator(Pageable pageable) {
        return switch (pageable.getSort().toString()) {
            case "title: ASC" -> Comparator.comparing(ItemDto::title);
            case "price: ASC" -> Comparator.comparingLong(ItemDto::price);
            default           -> Comparator.comparingLong(ItemDto::id);
        };
    }

    public Flux<ItemDto> findAll(String rawsearchstring, Pageable pageable) {
        return currentUserFacade.getUserId()
                .flatMapMany(userId -> findAllForUser(userId, rawsearchstring, pageable));
    }

    Flux<ItemDto> findAllForUser(Long userId, String rawsearchstring, Pageable pageable) {
        final String searchstring = rawsearchstring.trim();
        Comparator<ItemDto> compare = getComparator(pageable);
        return userCacheVersionService.getVersion()
                .flatMapMany(version -> {
                    String redisKey = getUserPageKey(userId, version);
                    String pageKey = getPageKey(searchstring, pageable);
                    return this.getCachedPageDto(redisKey, pageKey)
                            .map(PageDto::getItems)
                            .flatMapMany(Flux::fromIterable)
                            .sort(compare)
                            .switchIfEmpty(Flux.defer(() -> itemService.findAll(searchstring, pageable)
                                            .flatMap(item -> cartItemService.getInCartCount(userId, item.getId())
                                                    .map(count -> ItemToDtoMapper.toDto(item, count, UPLOAD_DIR))
                                            )
                                            .sort(compare))
                                    .collectList()
                                    .flatMap(list -> this.putCachedPageDto(redisKey, pageKey, userId, list).thenReturn(list))
                                    .flatMapMany(Flux::fromIterable));
                });
    }

    public Mono<PageDto> getCachedPageDto(String redisKey, String pageKey) {
        return pageRedisTemplate
                .opsForHash()
                .get(redisKey, pageKey)
                .cast(PageDto.class);
    }

    private Mono<Boolean> putCachedPageDto(String redisKey, String pageKey, Long userId, List<ItemDto> list) {
        log.info("put: KEY={}, pageKey={}, userId={}", redisKey, pageKey, userId);
        return pageRedisTemplate.opsForHash().put(redisKey, pageKey, new PageDto(userId, list))
                .flatMap(saved -> pageRedisTemplate.expire(redisKey, Duration.ofMinutes(10)).thenReturn(saved));
    }
    //методы для работы с Item
    //findItem - метод для "ручки"
    //findItemForUser - внутренний метод с определенным userId
    //getCachedItemDto и putCachedItemDto - внутренняя логика кэширования
    public Mono<ItemDto> findItem(Long itemId) {
        return currentUserFacade.getUserId()
                .flatMap(userId -> findItemForUser(userId, itemId));
    }

    Mono<ItemDto> findItemForUser(Long userId, Long itemId) {
        String redisKey="item:"+userId;
        return getCachedItemDto(redisKey, itemId.toString())
                .switchIfEmpty(Mono.defer(() -> itemService.findItemById(itemId))
                        .zipWhen(i -> cartItemService.getInCartCount(userId, i.getId()))
                        .map(x -> ItemToDtoMapper.toDto(x.getT1(), x.getT2(), UPLOAD_DIR))
                        .flatMap(itemDto -> this.putCachedItemDto(redisKey, itemId.toString(), itemDto).thenReturn(itemDto))
                );
    }

    private  Mono<ItemDto> getCachedItemDto(String redisKey, String itemKey) {
        return itemRedisTemplate.opsForHash()
                .get(redisKey, itemKey)
                .cast(ItemDto.class);
    }

    private Mono<Boolean> putCachedItemDto(String redisKey, String itemKey, ItemDto itemDto) {
        return itemRedisTemplate.opsForHash().put(redisKey, itemKey, itemDto)
                .flatMap(saved -> itemRedisTemplate.expire(redisKey, Duration.ofMinutes(10)).thenReturn(saved));
    }

    //Общее число товаров в каталоге
    public Mono<Long> count() {
        return itemService.count();
    }
}