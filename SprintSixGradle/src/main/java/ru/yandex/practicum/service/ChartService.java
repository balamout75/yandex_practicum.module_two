package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapper.ItemToDtoMapper;

import java.util.Comparator;

@Service
public class ChartService {

    private final CartItemService cartItemService;
    private final ItemService itemService;
    @Value("${images.path}")
    private String UPLOAD_DIR;

    public ChartService(ItemService itemService, CartItemService cartItemService) {
        this.itemService = itemService;
        this.cartItemService = cartItemService;
    }

    public Flux<ItemDto> findAll(Long userId, String searchstring, Pageable pageable) {
        searchstring = searchstring.trim();
        Comparator<ItemDto> compare = switch (pageable.getSort().toString()) {
            case "title: ASC" -> Comparator.comparing(ItemDto::title);
            case "price: ASC" -> Comparator.comparingLong(ItemDto::price);
            default           -> Comparator.comparingLong(ItemDto::id);
        };

        return itemService.findAll(userId, searchstring, pageable)
                .flatMap(item -> Mono.just(item).zipWhen(i -> cartItemService.getInCartCount(userId, i.getId())))
                .map(x -> ItemToDtoMapper.toDto(x.getT1(), x.getT2(), UPLOAD_DIR))
                .sort(compare);
    }

    public Mono<ItemDto> findItem(long userId, Long itemId) {
        return itemService.findItemById(itemId)
                .zipWhen(i -> cartItemService.getInCartCount(userId, i.getId()))
                .map(x -> ItemToDtoMapper.toDto(x.getT1(), x.getT2(), UPLOAD_DIR));
    }

    public Mono<Long> count() {
        return itemService.count();
    }
}