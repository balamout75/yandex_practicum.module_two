package ru.yandex.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.Item;

public interface ItemRepository extends ReactiveSortingRepository<Item, Long> {


    Mono<Long> count();

    Flux<Item> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String title1, Pageable pageable);

    Mono<Item> findById(Long itemId);

}
