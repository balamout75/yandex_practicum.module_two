package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /*
    public Flux<User> findAll() {
        return repository.findAll();
    }

    public Mono<User> findById(Long id) {
        return repository.findById(id);
    }

    public Mono<User> create(User user) {
        return repository.save(user);
    }

    public Mono<User> update(Long id, User patch) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Пользователь не найден: " + id)))
                .flatMap(user -> {
                    user.setFirstName(patch.getFirstName());
                    user.setLastName(patch.getLastName());
                    user.setAge(patch.getAge());
                    user.setActive(patch.isActive());
                    return repository.save(user);
                });
    }

    public Mono<Void> delete(Long id) {
        return repository.deleteById(id);
    }*/
} 