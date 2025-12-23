package ru.yandex.practicum.configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import java.io.IOException;

@TestConfiguration
public class EmbeddedRedisConfiguration {

    public static final int REDIS_PORT = 6381;

    private RedisServer redisServer;

    @PostConstruct
    void start() throws IOException {
        redisServer = RedisServer.builder()
                .port(REDIS_PORT)
                .setting("bind 127.0.0.1")
                .build();
        redisServer.start();
    }

    @PreDestroy
    void stop() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}