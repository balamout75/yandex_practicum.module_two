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

@Lazy
@Service
public class OrderService {
    private final UserService           userService;
    private final InOrderRepository     inOrderRepository;
    private final OrderRepository       orderRepository;
    private final InCartRepository      inCartRepository;
    private final FromCartToOrderMapper fromCartToOrderMapper;
    private final OrderToDtoMapper      orderToDtoMapper;

    public OrderService(UserService userService, OrderRepository orderRepository, InCartRepository inCartRepository, InOrderRepository inOrderRepository, FromCartToOrderMapper fromCartToOrderMapper, OrderToDtoMapper orderToDtoMapper) {
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.inCartRepository = inCartRepository;
        this.inOrderRepository = inOrderRepository;
        this.fromCartToOrderMapper = fromCartToOrderMapper;
        this.orderToDtoMapper = orderToDtoMapper;
    }

    @Transactional
    public long closeCart(Long userId) {
        User user = userService.getUser(userId);
        Order order = new Order(user);
        orderRepository.save(order);
        user.getInCarts().stream()
                .map(u-> fromCartToOrderMapper.toInOrder(order,u))
                .forEach(inOrderRepository::save);
        orderRepository.save(order);
        inCartRepository.deleteAll(user.getInCarts());
        return order.getId();
    }

    public List<OrderDto> findOrders(Long userId) {
        User user = userService.getUser(userId);
        return orderRepository.findByUser(user).stream()
                              .map(orderToDtoMapper::toDto)
                              .toList();
    }

    public OrderDto findOrder(Long userId, Long orderId) {
        Optional <Order> order = orderRepository.findById(orderId);
        return order.map(orderToDtoMapper::toDto).orElse(null);
    }

    public boolean exists(Long userId, Long orderId) {
        return orderRepository.existsById(orderId);
    }
}
