package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.mapper.OrderForming;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.CartItemRepository;
import ru.yandex.practicum.repository.OrderItemRepository;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository       orderRepository;
    private final OrderItemRepository   orderItemRepository;
    private final CartItemRepository    cartItemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Mono<OrderDto> findOrder(Long userId, Long orderId) {
        return orderItemRepository.findByUserAndOrder(orderId).collectList()
                .switchIfEmpty(Mono.empty())
                .mapNotNull(u -> OrderForming.toOrderDto(u, orderId));
    }

    public Mono<List<OrderDto>> findOrders(Long userId) {
        return orderItemRepository.findByUser().collectList()
                        .map(u -> {
                                Map<Long, List<OrdersItems>> myMap = u.stream().collect(Collectors.groupingBy(OrdersItems::orderid));
                                return myMap.entrySet().stream()
                                        .map(U -> OrderForming.toOrderDto(U.getValue(), U.getKey()))
                                        .toList();
                                });
    }

    public Mono <OrderItem> newOrderItem(long orderId, Long itemId, Long count) {
        return orderItemRepository.save(new OrderItem(orderId, itemId, count));
    }

    public Flux <CartItem> getCartItems(long userId) {
        return cartItemRepository.findByUserId(userId);
    }
    public Mono <Void> deleteCartItem(long userId, long itemId) {
        return cartItemRepository.deleteById(new CartItemId(userId, itemId));
    }

    public Mono<Long> closeCart(Long userId) {
        Mono<Long>      monoOrderId     =   orderRepository.getId().zipWhen(u -> orderRepository.save(new Order(u))).map(z ->z.getT2().getId());

        return monoOrderId.flatMapMany(orderId -> getCartItems(userId).flatMap(cartItem -> Mono.just(cartItem).zipWith(Mono.just(orderId))))
                                        .flatMap(x-> newOrderItem(x.getT2(), x.getT1().getItemId(), x.getT1().getCount())
                                                .zipWith(Mono.just(x.getT2()))
                                                .zipWhen(ex -> deleteCartItem(userId, ex.getT1().getItemId()).thenReturn(Mono.just("ok")))
                                        )
                                        .collectList()
                                        .map(list -> list.getFirst().getT1().getT2());
    }
}

