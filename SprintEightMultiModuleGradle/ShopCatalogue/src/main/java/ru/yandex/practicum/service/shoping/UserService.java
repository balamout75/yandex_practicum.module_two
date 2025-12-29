package ru.yandex.practicum.service.shoping;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.shoping.UserDto;
import ru.yandex.practicum.model.shoping.User;
import ru.yandex.practicum.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;

    //Сервисный класс для поиска userId по ключу из Keycloak
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    //@Cacheable(
    //        value = "user",
    //        key = "#sub"
    //)
    public Mono<UserDto> findOrCreate(String sub) {
        Mono<User> user = repository.findBySub(sub).switchIfEmpty(Mono.defer(() -> this.registerNewUser(sub)));
        return user.map(u -> new UserDto(u.getId(), u.getFirstName(), u.getLastName(), u.getSub()));
    }

    private Mono<User> registerNewUser(String sub) {
        return repository.getId()
                .flatMap(id ->repository.save(new User(id, sub)))
                .onErrorResume(DuplicateKeyException.class,e -> repository.findBySub(sub));
    }
}