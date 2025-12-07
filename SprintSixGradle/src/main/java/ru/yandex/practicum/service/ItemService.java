package ru.yandex.practicum.service;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapper.ItemToDtoMapper;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.repository.ItemRepository;

@Service
public class ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemService.class);
    private final CartService cartService;
    @Value("${images.path}")
    private String UPLOAD_DIR;

    private final ItemRepository repository;

    public ItemService(ItemRepository repository, CartService cartService) {
        this.repository = repository;
        this.cartService = cartService;
    }

    public Flux<ItemDto> findAll(Long userId, String searchstring, Pageable pageable) {
        return repository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchstring, searchstring, pageable)
                .flatMap(item -> Mono.just(item).zipWhen(i -> cartService.getInCartCount(userId, i.getId())))
                .map(x ->ItemToDtoMapper.toDto(x.getT1(), x.getT2(), UPLOAD_DIR));
    }

    public Mono<ItemDto> findItem(long userId, Long itemId) {
        return repository.findById (itemId)
                .zipWhen(i -> cartService.getInCartCount(userId, i.getId()))
                .map(x ->ItemToDtoMapper.toDto(x.getT1(), x.getT2(), UPLOAD_DIR));
    }

    public Mono<Long> count() {
        return repository.count();
    }

    public Mono<Item> findItemById(Long itemId) {
        return repository.findById(itemId);

    }
}