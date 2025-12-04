package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.mapper.OrderToDtoMapper;
import ru.yandex.practicum.repository.OrderItemRepository;

import java.util.*;

@Service
public class OrderService {

    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public Flux<OrderDto> findOrders(Long userId) {
        return orderItemRepository.findByUser(userId).map(OrderToDtoMapper::toDto);
    }

    public Mono<OrderDto> findOrder(Long userId, Long orderId) {
        return orderItemRepository.findByUserAndOrder(userId,orderId).map(OrderToDtoMapper::toDto);
    }

}
