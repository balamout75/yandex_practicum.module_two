package ru.yandex.practicum.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.yandex.practicum.dto.shoping.PageDto;

public interface PageRedisRepository
        extends ReactiveCrudRepository<PageDto, String> {
}