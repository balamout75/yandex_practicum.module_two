package ru.yandex.practicum.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapper.ItemToDtoMapper;
import ru.yandex.practicum.repository.ItemRepository;

@Service
public class ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemService.class);
    @Value("${images.path}")
    private String UPLOAD_DIR;

    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public Flux<ItemDto> findAll(String searchstring, Pageable pageable) {
        if (searchstring.isBlank()){
            return switch ()
                    repository.findByDescription(sort, sort).map(u -> ItemToDtoMapper.toDto(u, UPLOAD_DIR));
    }

    public Mono<ItemDto> findItem(long userId, Long itemId) {
        return repository.findById(userId, itemId).map(u -> ItemToDtoMapper.toDto(u, UPLOAD_DIR));
    }

    public Mono<Long> count() {
        return repository.count();
    }
}