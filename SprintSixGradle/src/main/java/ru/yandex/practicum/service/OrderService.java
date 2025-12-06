package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
    private record MyStupideless (
        OrderItem oi,
        Void      x,
        Order     order) {
    }

    public Mono<Long> moveRecords(List<UsersItems> items, Order order) {
        items.stream().
                map(u -> {
                    Mono<OrderItem> orderItems = orderItemRepository.save(new OrderItem(1L, u.id()));
                    Mono<Void>      none       = cartItemRepository.deleteById(new CartItemId(1L,u.id()));
                    Mono<Order>     savedorder = orderRepository.save(order);
                    return Mono.zip(orderItems, none, savedorder)
                            .map(x-> new MyStupideless(x.getT1(), x.getT2(), x.getT3()))
                            .map(y -> y.order().getId());})
                .forEach(System.out::println);
        return Mono.just(order.getId());
    }

    /*public Mono<Long> closeCart(Long userId) {
        return cartItemRepository.inCartItems(userId).collectList()
                .switchIfEmpty(Mono.empty())
                .zipWith(orderRepository.getId().map(Order::new))
                .flatMap(tuple -> moveRecords(tuple.getT1(),tuple.getT2()));
    }*/
    public Mono<Long> closeCart(Long userId) {
        return orderRepository.getId()
                        .map(Order::new)
                        .flatMap(orderRepository::save)
                        .map(Order::getId);
    }

}

/*

                                        .zipWith(cartItemRepository.deleteById(new CartItemId(1L,item.id())))

                                .map(ee -> {ee.})
                                .getFirst().;

                        });
 */