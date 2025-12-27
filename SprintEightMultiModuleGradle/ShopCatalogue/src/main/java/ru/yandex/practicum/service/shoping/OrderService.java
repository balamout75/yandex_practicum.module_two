package ru.yandex.practicum.service.shoping;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;
import ru.yandex.practicum.dto.shoping.ItemDto;
import ru.yandex.practicum.dto.shoping.OrderDto;
import ru.yandex.practicum.mapper.OrderToDtoMapper;
import ru.yandex.practicum.model.shoping.CartItem;
import ru.yandex.practicum.model.shoping.CartItemId;
import ru.yandex.practicum.model.shoping.Order;
import ru.yandex.practicum.model.shoping.OrderItem;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.Comparator;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final ItemService itemService;
    private final CartItemService cartItemService;
    private final UserCacheVersionService userCacheVersionService;
    private final TransactionalOperator txOperator;


    public OrderService(OrderRepository orderRepository,
                        OrderItemService orderItemService,
                        ItemService itemService,
                        CartItemService cartItemService,
                        UserCacheVersionService userCacheVersionService,
                        TransactionalOperator txOperator) {
        this.orderRepository = orderRepository;
        this.orderItemService = orderItemService;
        this.itemService = itemService;
        this.cartItemService = cartItemService;
        this.userCacheVersionService = userCacheVersionService;
        this.txOperator = txOperator;
    }


    public Mono<OrderDto> findOrder(Long userId, Long orderId) {
        return orderItemService.findByOrder(orderId)
                .flatMap(oi -> itemService.findItemById(oi.getItemId())
                        .map(item -> Tuples.of(oi, item)))
                .collectList()
                .map(list -> OrderToDtoMapper.toDto2(list, orderId));
    }

    public Flux<OrderDto> findOrders(Long userId) {
        return orderItemService.findByUser(userId)
                .flatMap(oi -> itemService.findItemById(oi.getItemId())
                        .map(item -> Tuples.of(oi, item)))
                .collectMultimap(t -> t.getT1().getOrderId())
                .flatMapMany(map ->
                        Flux.fromIterable(
                                map.entrySet().stream()
                                        .map(e -> OrderToDtoMapper.toDto2(e.getValue().stream().toList(), e.getKey()))
                                        .toList()
                        )
                );
    }

    private Mono<OrderItem> newOrderItem(long orderId, Long itemId, Long count) {
        return orderItemService.save(new OrderItem(orderId, itemId, count));
    }

    private Flux<CartItem> getCartItems(long userId) {
        return cartItemService.findByUserId(userId);
    }

    public Mono<Void> clearCart(Long userId) {
        return cartItemService.deleteByUserId(userId);
    }

    private Mono<Void> copyCartItems(Long userId, Long orderId) {
        return getCartItems(userId)
                .flatMap(cartItem ->
                        newOrderItem(
                                orderId,
                                cartItem.getItemId(),
                                cartItem.getCount()
                        )
                )
                .then();
    }

    private Mono<Void> createOrder(Long userId, Long orderId) {
        return orderRepository.save(new Order(userId, orderId)).then();
    }


    public Mono<Long> closeCart(Long userId) {
        return txOperator.transactional(
                orderRepository.getId()
                        .flatMap(orderId ->
                                createOrder(userId, orderId)
                                        .then(copyCartItems(userId, orderId))
                                        .then(clearCart(userId))
                                        .then(userCacheVersionService.increment(userId))
                                        .thenReturn(orderId)
                        )
        );
    }

}

