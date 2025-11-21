package ru.yandex.practicum.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.mapping.FromCartToOrderMapper;
import ru.yandex.practicum.mapping.OrderToDtoMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.*;
import java.util.*;
import java.util.stream.Collectors;

@Lazy
@Service
public class OrderService {
    private final InOrderRepository     inOrderRepository;
    private final OrderRepository       orderRepository;
    private final InCartRepository      inCartRepository;
    private final FromCartToOrderMapper fromCartToOrderMapper;
    private final OrderToDtoMapper orderToDtoMapper;

    public OrderService(OrderRepository orderRepository, InCartRepository inCartRepository, InOrderRepository inOrderRepository, FromCartToOrderMapper fromCartToOrderMapper, OrderToDtoMapper orderToDtoMapper) {
        this.orderRepository = orderRepository;
        this.inCartRepository = inCartRepository;
        this.inOrderRepository = inOrderRepository;
        this.fromCartToOrderMapper = fromCartToOrderMapper;
        this.orderToDtoMapper = orderToDtoMapper;
    }

    @Transactional
    public long closeCart(User user) {
        Order order = new Order();
        order.setUser(user);
        orderRepository.save(order);
        fromCartToOrderMapper.setOrder(order);
        user.getInCarts().stream()
                .map(fromCartToOrderMapper::toInOrder)
                .map(inOrderRepository::save)
                .collect(Collectors.toSet());
        orderRepository.save(order);
        inCartRepository.deleteAll(user.getInCarts());
        return order.getId();
    }

    public List<OrderDto> findOrders(User user) {
        return orderRepository.findByUser(user).stream()
                              .map(orderToDtoMapper::toDto)
                              .toList();
    }

    public OrderDto findOrder(long userId, Long orderId) {
        Order order = orderRepository.findById(orderId);
        return orderToDtoMapper.toDto(order);
    }
}
