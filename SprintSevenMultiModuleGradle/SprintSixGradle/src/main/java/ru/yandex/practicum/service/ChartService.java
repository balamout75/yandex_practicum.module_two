package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.dto.PageDto;
import ru.yandex.practicum.mapper.ItemToDtoMapper;
import ru.yandex.practicum.model.Item;

import java.util.*;

@Service
public class ChartService {

    private final CartItemService cartItemService;
    private final ItemService itemService;
    @Value("${images.path}")
    private String UPLOAD_DIR;
    //private static final String KEY = "page23";

    @Autowired
    private ReactiveHashOperations<String, Integer, PageDto> pageDtoHshOperations;

    public ChartService(ItemService itemService, CartItemService cartItemService) {
        this.itemService = itemService;
        this.cartItemService = cartItemService;
    }

    public Flux<ItemDto> findAll(Long userId, String rawsearchstring, Pageable pageable) {
        final String searchstring = rawsearchstring.trim();
        final String KEY = "page"+userId;
        Integer pageHash=Objects.hash(userId, searchstring, pageable.toString());

        Comparator<ItemDto> compare = switch (pageable.getSort().toString()) {
            case "title: ASC" -> Comparator.comparing(ItemDto::title);
            case "price: ASC" -> Comparator.comparingLong(ItemDto::price);
            default           -> Comparator.comparingLong(ItemDto::id);
        };

        return pageDtoHshOperations.get(KEY, pageHash)
                .map(PageDto::getItems)
                .flatMapMany(Flux::fromIterable)
                .sort(compare)
                .switchIfEmpty(Flux.defer(() -> itemService.findAll(userId, searchstring, pageable)
                                .flatMap(item -> Mono.just(item).zipWhen(i -> cartItemService.getInCartCount(userId, i.getId())))
                                .map(x -> ItemToDtoMapper.toDto(x.getT1(), x.getT2(), UPLOAD_DIR))
                                .sort(compare))
                                .collectList()
                                .flatMap(list -> this.put(KEY, pageHash, list).thenReturn(list))
                                .flatMapMany(Flux::fromIterable));
    }

    private Mono<Boolean> put(String KEY, Integer hashKey, List<ItemDto> list) {
        return pageDtoHshOperations.put(KEY, hashKey, new PageDto(list));
    }

    @Cacheable(
            value = "item",
            key   = "#userId.toString().concat('-').concat(#itemId.toString)"
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