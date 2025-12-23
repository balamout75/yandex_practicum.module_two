package ru.yandex.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.shoping.Item;

public interface ItemRepository extends ReactiveSortingRepository<Item, Long> {


    Flux<Item> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description, Pageable pageable);

    Mono<Item> findById(Long itemId);

    Mono<Long> count();

    Flux<Item> findAllBy(Pageable pageable);
}
