package ru.yandex.practicum.service.shoping;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.shoping.User;
import ru.yandex.practicum.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Mono<User> findOrCreate(String sub) {
        return repository.findBySub(sub)
                .switchIfEmpty(Mono.defer(() -> this.registerNewUser(sub)));
    }

    private Mono<User> registerNewUser(String sub) {
        return repository.getId()
                .flatMap(id ->repository.save(new User(id, sub)))
                .onErrorResume(DuplicateKeyException.class,e -> repository.findBySub(sub));
    }
}