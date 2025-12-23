package ru.yandex.practicum.service.shoping;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class UserCacheVersionService {

    private static final String USER_VERSION_KEY = "user:version:";
    private static final Duration VERSION_TTL = Duration.ofMinutes(30);

    private final ReactiveRedisTemplate<String, Long> redis;

    public UserCacheVersionService(ReactiveRedisTemplate<String, Long> redis) {
        this.redis = redis;
    }

    public Mono<Long> getVersion(Long userId) {
        return redis.opsForValue()
                .get(USER_VERSION_KEY + userId)
                .switchIfEmpty(Mono.just(0L));
    }

    public Mono<Long> increment(Long userId) {
        String key = USER_VERSION_KEY + userId;
        return redis.opsForValue()
                .increment(key)
                .flatMap(version -> redis.expire(key, VERSION_TTL).thenReturn(version));
    }
}
