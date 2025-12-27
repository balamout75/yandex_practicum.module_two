package ru.yandex.practicum.service.shoping;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.security.CurrentUserFacade;
import java.time.Duration;

@Service
public class UserCacheVersionService {

    private static final String USER_VERSION_KEY = "user:version:";
    private static final Duration VERSION_TTL = Duration.ofMinutes(30);
    private final ReactiveRedisTemplate<String, Long> redis;
    private final CurrentUserFacade currentUserFacade;

    //Класс версирования страниц каталога для каждого пользователя
    public UserCacheVersionService(ReactiveRedisTemplate<String, Long> redis,
                                   CurrentUserFacade currentUserFacade) {
        this.redis = redis;
        this.currentUserFacade = currentUserFacade;
    }

    Mono<Long> getVersion() {
        return currentUserFacade.getUserId()
                .flatMap(userId -> redis.opsForValue().get(USER_VERSION_KEY + userId)
                        .switchIfEmpty(Mono.just(0L)));
    }

    Mono<Long> increment() {
        return currentUserFacade.getUserId()
                .flatMap(userId -> redis.opsForValue().increment(USER_VERSION_KEY + userId)
                        .flatMap(version -> redis.expire(USER_VERSION_KEY + userId, VERSION_TTL).thenReturn(version)));
    }
}
