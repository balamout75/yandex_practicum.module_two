package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.mapper.OrderToDtoMapper;
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
    private final ItemService itemService;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, CartItemRepository cartItemRepository, ItemService itemService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.itemService = itemService;
    }

    public Mono<OrderDto> findOrder(Long userId, Long orderId) {
        return orderItemRepository.findByOrder(orderId)
                .flatMap(orderItem->Mono.just(orderItem).zipWhen((oi -> itemService.findItemById(oi.getItemId()))))
                .collectList()
                .map(list ->  OrderToDtoMapper.toDto2(list,orderId));
    }

    public Flux<OrderDto> findOrders(Long userId) {
        return orderItemRepository.findByUser(userId)
                .flatMap(orderItem->Mono.just(orderItem).zipWhen((oi -> itemService.findItemById(oi.getItemId()))))
                .collectList()
                .map(tuple ->  {
                    Map<Long, List<Tuple2<OrderItem, Item>>> myMap = tuple.stream().collect(Collectors.groupingBy(f->f.getT1().getOrderId()));
                    return myMap.entrySet().stream()
                            .map(x -> OrderToDtoMapper.toDto2(x.getValue(), x.getKey()))
                            .toList();
                })
                .flatMapMany(Flux::fromIterable);
/*


        return orderItemRepository.findByUser(userId).collectList()
                        .map(u -> {
                                Map<Long, List<OrderItem>> myMap = u.stream().collect(Collectors.groupingBy(OrderItem::getOrderId));
                                return myMap.entrySet().stream()
                                        .map(entry -> OrderToDtoMapper.toDto(entry.getValue(), entry.getKey()))
                                        .toList();
                                });*/
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

