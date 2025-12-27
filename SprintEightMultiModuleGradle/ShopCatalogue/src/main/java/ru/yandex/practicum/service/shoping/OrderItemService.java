package ru.yandex.practicum.service.shoping;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.shoping.OrderItem;
import ru.yandex.practicum.repository.OrderItemRepository;


@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    Flux<OrderItem> findByOrder(Long orderId) {
        return orderItemRepository.findByOrder(orderId);
    }

    Flux<OrderItem> findByUser(Long userId) { return orderItemRepository.findByUser(userId); }

    Mono<OrderItem> save(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }
}

