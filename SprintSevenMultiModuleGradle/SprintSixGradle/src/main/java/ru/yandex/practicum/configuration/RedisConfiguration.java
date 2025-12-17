package ru.yandex.practicum.configuration;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.yandex.practicum.dto.shoping.ItemDto;
import ru.yandex.practicum.dto.shoping.PageDto;


import java.time.Duration;
import java.time.temporal.ChronoUnit;


@Configuration
public class RedisConfiguration {

    @Bean
    public RedisCacheManagerBuilderCustomizer itemCacheCustomizer() {
        return builder -> builder
                .withCacheConfiguration("itemDto",                                         // Имя кеша
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.of(30, ChronoUnit.MINUTES))  // TTL
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                        new JacksonJsonRedisSerializer<>(ItemDto.class)
                                        //new GenericJacksonJsonRedisSerializer(ItemDto.class)
                                )
                        )
        );
    }

    @Bean
    public ReactiveHashOperations<String, Integer, PageDto> pageDtoHashOperations(
            ReactiveRedisConnectionFactory connectionFactory) {

        // Define serializers
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericToStringSerializer<Integer> hashKeySerializer = new GenericToStringSerializer<>(Integer.class);
        JacksonJsonRedisSerializer<PageDto> hashValueSerializer = new JacksonJsonRedisSerializer<>(PageDto.class);

        // Build the serialization context
        RedisSerializationContext<String, PageDto> serializationContext =
                RedisSerializationContext.<String, PageDto>newSerializationContext(keySerializer)
                        .hashKey(hashKeySerializer)
                        .hashValue(hashValueSerializer)
                        .build();

        // Create the reactive template and get hash operations
        ReactiveRedisTemplate<String, PageDto> reactiveRedisTemplate = new ReactiveRedisTemplate<>(
                connectionFactory, serializationContext);

        return reactiveRedisTemplate.opsForHash();
    }
}