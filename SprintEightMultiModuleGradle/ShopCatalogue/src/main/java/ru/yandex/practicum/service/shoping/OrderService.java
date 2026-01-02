package ru.yandex.practicum.service.shoping;

import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;
import ru.yandex.practicum.dto.shoping.OrderDto;
import ru.yandex.practicum.mapper.OrderToDtoMapper;
import ru.yandex.practicum.model.shoping.CartItem;
import ru.yandex.practicum.model.shoping.Order;
import ru.yandex.practicum.model.shoping.OrderItem;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.security.CurrentUserFacade;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final ItemService itemService;
    private final CartItemService cartItemService;
    private final UserCacheVersionService userCacheVersionService;
    private final CurrentUserFacade currentUserFacade;
    private final TransactionalOperator txOperator;

    public OrderService(OrderRepository orderRepository,
                        OrderItemService orderItemService,
                        ItemService itemService,
                        CartItemService cartItemService,
                        UserCacheVersionService userCacheVersionService, CurrentUserFacade currentUserFacade,
                        TransactionalOperator txOperator) {
        this.orderRepository = orderRepository;
        this.orderItemService = orderItemService;
        this.itemService = itemService;
        this.cartItemService = cartItemService;
        this.userCacheVersionService = userCacheVersionService;
        this.currentUserFacade = currentUserFacade;
        this.txOperator = txOperator;
    }

    //Работа с заказами по orderId
    public Mono<OrderDto> findOrder(Long orderId) {
        return orderItemService.findByOrder(orderId)
                .flatMap(oi -> itemService.findItemById(oi.getItemId())
                        .map(item -> Tuples.of(oi, item)))
                .collectList()
                .map(list -> OrderToDtoMapper.toDto2(list, orderId));
    }

    //Работа с заказами по userId
    public Flux<OrderDto> findOrders() {
        return currentUserFacade.getUserId()
                .flatMapMany(this::findOrdersForUser);
    }

    Flux<OrderDto> findOrdersForUser(Long userId) {
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

    //методы для закрытия корзины
    private Mono<OrderItem> newOrderItem(long orderId, Long itemId, Long count) {
        return orderItemService.save(new OrderItem(orderId, itemId, count));
    }

    private Flux<CartItem> getCartItems(long userId) {
        return cartItemService.findForUser(userId);
    }

    public Mono<Void> clearCart(Long userId) {
        return cartItemService.deleteForUser(userId);
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


    public Mono<Long> closeCart() {
        return currentUserFacade.getUserId()
                .flatMap(this::closeCartForUser);
    }
    Mono<Long> closeCartForUser(Long userId) {
        return txOperator.transactional(
                orderRepository.getId()
                        .flatMap(orderId ->
                                createOrder(userId, orderId)
                                        .then(copyCartItems(userId, orderId))
                                        .then(clearCart(userId))
                                        .then(userCacheVersionService.increment())
                                        .thenReturn(orderId)
                        )
        );
    }
}

