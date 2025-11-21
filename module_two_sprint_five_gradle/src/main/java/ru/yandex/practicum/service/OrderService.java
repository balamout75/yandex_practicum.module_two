package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.mapping.InCartToInOrderMapper;
import ru.yandex.practicum.mapping.OrderEntityMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final InOrderRepository inOrderRepository;

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final InCartRepository inCartRepository;
    private final InCartToInOrderMapper inCartToInOrderMapper;
    private final OrderEntityMapper orderEntityMapper;

    public OrderService(UserRepository userRepository, OrderRepository orderRepository, InCartRepository inCartRepository, InOrderRepository inOrderRepository, InCartToInOrderMapper inCartToInOrderMapper, OrderEntityMapper orderEntityMapper) {

        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.inCartRepository = inCartRepository;
        this.inOrderRepository = inOrderRepository;
        this.inCartToInOrderMapper = inCartToInOrderMapper;
        this.orderEntityMapper = orderEntityMapper;
    }

    @Transactional
    public long closeCart(long userId) {
        User user = userRepository.findById(userId);
        Order order = new Order();
        order.setUser(user);
        orderRepository.save(order);
        inCartToInOrderMapper.setOrder(order);
        user.getInCarts().stream()
                .map(inCartToInOrderMapper::toInOrder)
                .map(inOrderRepository::save)
                .collect(Collectors.toSet());
        orderRepository.save(order);
        inCartRepository.deleteAll(user.getInCarts());
        return order.getId();
    }

    public List<OrderDto> findOrders(long userId) {
        User user = userRepository.findById(userId);
        return orderRepository.findByUser(user).stream()
                              .map(orderEntityMapper::toDto)
                              .toList();

    }

    public OrderDto findOrder(long userId, Long orderId) {
        Order order = orderRepository.findById(orderId);
        return orderEntityMapper.toDto(order);
    }
}
