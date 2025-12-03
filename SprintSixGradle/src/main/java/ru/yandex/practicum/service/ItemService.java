package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapper.ItemToDtoMapper;
import ru.yandex.practicum.repository.ItemRepository;

@Service
public class ItemService {

    @Value("${images.path}")
    private String UPLOAD_DIR;

    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public Flux<ItemDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(u -> ItemToDtoMapper.toDto(u, UPLOAD_DIR));
    }

    public Mono<ItemDto> findItem(long userId, Long itemId) {
        return repository.findById(userId, itemId).map(u -> ItemToDtoMapper.toDto(u, UPLOAD_DIR));
    }


    /*
    public Mono<Item> findById(Long id) {
        return repository.findById(id);
    }

    public Mono<Item> create(Item item) {
        return repository.save(item);
    }
    */
    /*public Mono<User> update(Long id, User patch) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Пользователь не найден: " + id)))
                .flatMap(user -> {
                    user.setFirstName(patch.getFirstName());
                    user.setLastName(patch.getLastName());
                    user.setAge(patch.getAge());
                    user.setActive(patch.isActive());
                    return repository.save(user);
                });
    }*/

    public Mono<Void> delete(Long id) {
        return repository.deleteById(id);
    }

}