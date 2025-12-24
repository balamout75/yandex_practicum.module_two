package ru.yandex.practicum.service.shoping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.shoping.ItemDto;
import ru.yandex.practicum.dto.shoping.PageDto;
import ru.yandex.practicum.mapper.ItemToDtoMapper;

import java.util.*;

@Service
public class CatalogueService {

    private final CartItemService cartItemService;
    private final ItemService itemService;
    @Value("${images.path}")
    private String UPLOAD_DIR;

    private static final String KEY = "page";

    @Autowired
    private ReactiveHashOperations<String, String, PageDto> pageDtoHshOperations;

    @Autowired
    private UserCacheVersionService userCacheVersionService;

    public CatalogueService(ItemService itemService, CartItemService cartItemService) {
        this.itemService = itemService;
        this.cartItemService = cartItemService;
    }

    private String getPageKey(Long userId, Long version, String search, Pageable pageable ) {
        return "page:%d:%d:%s:%s".formatted(
                userId,
                version,
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
                    String pageKey = getPageKey(userId, version, searchstring, pageable);
                    return pageDtoHshOperations.get(KEY, pageKey)
                            .map(PageDto::getItems)
                            .flatMapMany(Flux::fromIterable)
                            .sort(compare)
                            .switchIfEmpty(Flux.defer(() -> itemService.findAll(userId, searchstring, pageable)
                                            .flatMap(item -> cartItemService.getInCartCount(userId, item.getId())
                                                            .map(count -> ItemToDtoMapper.toDto(item, count, UPLOAD_DIR))
                                            )
                                            .sort(compare))
                                    .collectList()
                                    .flatMap(list -> this.put(KEY, pageKey, userId, list).thenReturn(list))
                                    .flatMapMany(Flux::fromIterable));
                });
    }

    private Mono<Boolean> put(String KEY, String pageKey, Long userId, List<ItemDto> list) {
        return pageDtoHshOperations.put(KEY, pageKey, new PageDto(userId, list));
    }

    @Cacheable(
            value = "item",
            key = "#userId + '-' + #itemId"
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