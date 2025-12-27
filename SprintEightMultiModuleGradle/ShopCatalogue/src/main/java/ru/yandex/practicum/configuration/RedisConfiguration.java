package ru.yandex.practicum.configuration;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.yandex.practicum.dto.shoping.ItemDto;
import ru.yandex.practicum.dto.shoping.PageDto;
import ru.yandex.practicum.dto.shoping.UserDto;

import java.time.Duration;

@Configuration
public class RedisConfiguration {

    @Bean
    public ReactiveRedisTemplate<String, Long> reactiveRedisLongTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        RedisSerializationContext<String, Long> context =
                RedisSerializationContext.<String, Long>newSerializationContext(new StringRedisSerializer())
                        .value(new GenericToStringSerializer<>(Long.class))
                        .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer itemCacheCustomizer() {
        return builder -> builder
                .withCacheConfiguration("item",                                          // Имя кеша
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10))  // TTL
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                        new JacksonJsonRedisSerializer<>(ItemDto.class)
                                )
                        )
        );
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer userCacheCustomizer() {
        return builder -> builder
                .withCacheConfiguration("user",                                        // Имя кеша
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10))  // TTL
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                                new JacksonJsonRedisSerializer<>(UserDto.class)
                                        )
                                )
                );
    }

    @Bean
    public ReactiveRedisTemplate<String, PageDto> pageRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericToStringSerializer<String> hashKeySerializer = new GenericToStringSerializer<>(String.class);
        JacksonJsonRedisSerializer<PageDto> valueSerializer = new JacksonJsonRedisSerializer<>(PageDto.class);

        RedisSerializationContext<String, PageDto> context = RedisSerializationContext.<String, PageDto>newSerializationContext(keySerializer)
                        .hashKey(hashKeySerializer)
                        .hashValue(valueSerializer)
                        .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}