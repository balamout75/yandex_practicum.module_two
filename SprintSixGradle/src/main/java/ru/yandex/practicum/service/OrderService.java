package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.mapper.OrderForming;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderItem;
import ru.yandex.practicum.model.OrdersItems;
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
                .mapNotNull(OrderForming::toOrderDto);
    }

    public Mono<List<OrderDto>> findOrders(Long userId) {
        return orderItemRepository.findByUser().collectList()
                        .map(u -> {
                                Map<Long, List<OrdersItems>> myMap = u.stream().collect(Collectors.groupingBy(OrdersItems::orderid));
                                List<List <OrdersItems>> list = myMap.values().stream().toList();
                                return list.stream()
                                        .map(OrderForming::toOrderDto)
                                        .toList();
                                });
    }

    public Mono<Long> closeCart(Long userId) {
        cartItemRepository.inCartItems(userId)
                .switchIfEmpty(Mono.empty())
                .zipWith(orderRepository.save(new Order()))
                .collectList()
                .map(u -> {
                    u.stream().map(tuple -> tuple.getT1())


                    OrderItem oi = new OrderItem(tuple.getT1)
                })

                .
        return Mono.just(userId);
    }

/*
        User user = userService.getUser(userId);
        Order order = orderRepository.save(new Order(user));
        user.getInCarts().stream()
                .map(u-> fromCartToOrderMapper.toInOrder(order,u))
                .forEach(inOrderRepository::save);
        //orderRepository.save(order);
        inCartRepository.deleteAll(user.getInCarts());
        userRepository.save(user);
        return order.getId();
 */

}
