package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.mapper.OrderToDtoMapper;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.OrderItemRepository;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final ItemService itemService;
    private final CartItemService cartItemService;

    public OrderService(OrderRepository orderRepository, OrderItemService orderItemService,
                        ItemService itemService, CartItemService cartItemService) {
        this.orderRepository = orderRepository;
        this.orderItemService = orderItemService;
        this.itemService = itemService;
        this.cartItemService = cartItemService;
    }

    public Mono<OrderDto> findOrder(Long userId, Long orderId) {
        return orderItemService.findByOrder(orderId)
                .flatMap(orderItem -> Mono.just(orderItem)
                                .zipWhen((oi -> itemService.findItemById(oi.getItemId()))))
                .collectList().map(list -> OrderToDtoMapper.toDto2(list, orderId));
    }

    public Flux<OrderDto> findOrders(Long userId) {
        return orderItemService.findByUser(userId)
                .flatMap(orderItem -> Mono.just(orderItem).
                        zipWhen(oi -> itemService.findItemById(oi.getItemId())))
                .collectList()
                .map(tuples -> {
                    Map<Long, List<Tuple2<OrderItem, Item>>> myMap =
                            tuples.stream().collect(Collectors.groupingBy(f -> f.getT1().getOrderId()));
                    return myMap.entrySet().stream().map(x -> OrderToDtoMapper.toDto2(x.getValue(), x.getKey())).toList();})
                .flatMapMany(Flux::fromIterable);
    }

    private Mono<OrderItem> newOrderItem(long orderId, Long itemId, Long count) {
        return orderItemService.save(new OrderItem(orderId, itemId, count));
    }

    private Flux<CartItem> getCartItems(long userId) {
        return cartItemService.findByUserId(userId);
    }

    private Mono<Void> deleteCartItem(long userId, long itemId) {
        return cartItemService.deleteById(new CartItemId(userId, itemId));
    }

    public Mono<Long> closeCart(Long userId) {
        Mono<Long> monoOrderId = orderRepository.getId().zipWhen(u -> orderRepository.save(new Order(u))).map(z -> z.getT2().getId());
        return monoOrderId.flatMapMany(orderId -> getCartItems(userId).flatMap(cartItem -> Mono.just(cartItem).zipWith(Mono.just(orderId))))
                .flatMap(x -> newOrderItem(x.getT2(), x.getT1().getItemId(), x.getT1().getCount())
                        .zipWith(Mono.just(x.getT2()))
                        .zipWhen(ex -> deleteCartItem(userId, ex.getT1().getItemId())
                                .thenReturn(Mono.just("ok"))))
                .collectList().map(list -> list.getFirst().getT1().getT2());
    }
}

