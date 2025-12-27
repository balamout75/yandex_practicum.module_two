package ru.yandex.practicum.service.shoping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.shoping.ItemDto;
import ru.yandex.practicum.dto.shoping.PageDto;
import ru.yandex.practicum.mapper.ItemToDtoMapper;

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

    private final UserCacheVersionService userCacheVersionService;

    public CatalogueService(ItemService itemService,
                            CartItemService cartItemService,
                            ReactiveRedisTemplate<String, PageDto> pageRedisTemplate,
                            UserCacheVersionService userCacheVersionService) {
        this.itemService = itemService;
        this.cartItemService = cartItemService;
        this.pageRedisTemplate = pageRedisTemplate;
        this.userCacheVersionService = userCacheVersionService;
    }

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

    public Flux<ItemDto> findAll(Long userId, String rawsearchstring, Pageable pageable) {
        final String searchstring = rawsearchstring.trim();
        Comparator<ItemDto> compare = getComparator(pageable);

        return userCacheVersionService.getVersion(userId)
                .flatMapMany(version -> {
                    String redisKey = getUserPageKey(userId, version);
                    String pageKey  = getPageKey(searchstring, pageable);
                    return this.get(redisKey, pageKey)
                            .map(PageDto::getItems)
                            .flatMapMany(Flux::fromIterable)
                            .sort(compare)
                            .switchIfEmpty(Flux.defer(() -> itemService.findAll(userId, searchstring, pageable)
                                            .flatMap(item -> cartItemService.getInCartCount(userId, item.getId())
                                                            .map(count -> ItemToDtoMapper.toDto(item, count, UPLOAD_DIR))
                                            )
                                            .sort(compare))
                                    .collectList()
                                    .flatMap(list -> this.put(redisKey, pageKey, userId, list).thenReturn(list))
                                    .flatMapMany(Flux::fromIterable));
                });
    }

    public Mono<PageDto> get(String redisKey, String pageKey) {
        return pageRedisTemplate
                .opsForHash()
                .get(redisKey, pageKey)
                .cast(PageDto.class);
    }

    private Mono<Boolean> put(String redisKey, String pageKey, Long userId, List<ItemDto> list) {
        log.info("put: KEY={}, pageKey={}, userId={}", redisKey, pageKey, userId);
        return pageRedisTemplate.opsForHash().put(redisKey, pageKey, new PageDto(userId, list))
                .flatMap(saved -> pageRedisTemplate.expire(redisKey, Duration.ofMinutes(10)).thenReturn(saved));
    }

    @Cacheable(
            value = "item",
            key = "#userId + ':' + @userCacheVersionService.getVersion(#userId).block() + ':' + #itemId"
    )
    public Mono<ItemDto> findItem(long userId, Long itemId) {
        return itemService.findItemById(itemId)
                .zipWhen(i -> cartItemService.getInCartCount(userId, i.getId()))
                .map(x -> ItemToDtoMapper.toDto(x.getT1(), x.getT2(), UPLOAD_DIR));
    }

    public Mono<Long> count() {
        return itemService.count();
    }
}