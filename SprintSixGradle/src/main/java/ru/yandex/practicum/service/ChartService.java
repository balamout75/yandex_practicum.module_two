package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapper.ItemToDtoMapper;

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
        return itemService.findAll(userId, searchstring, pageable)
                .flatMap(item -> Mono.just(item).zipWhen(i -> cartItemService.getInCartCount(userId, i.getId())))
                .map(x -> ItemToDtoMapper.toDto(x.getT1(), x.getT2(), UPLOAD_DIR));
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