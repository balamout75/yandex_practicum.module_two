package ru.yandex.practicum.service.shoping;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.shoping.Item;
import ru.yandex.practicum.repository.ItemRepository;

@Service
public class ItemService {

    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    Flux<Item> findAll(String searchstring, Pageable pageable) {
        return searchstring.isBlank() ? repository.findAllBy(pageable) :
               repository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchstring, searchstring, pageable);
    }

    public Mono<Long> count() {
        return repository.count();
    }

    public Mono<Item> findItemById(Long itemId) {
        return repository.findById(itemId);

    }
}