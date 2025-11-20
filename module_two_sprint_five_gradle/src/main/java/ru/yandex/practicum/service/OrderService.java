package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.CartDto;
import ru.yandex.practicum.dto.ItemDto;

import ru.yandex.practicum.mapping.ItemEntityMapper;

import ru.yandex.practicum.model.InCart;
import ru.yandex.practicum.model.InOrder;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    //@Value("${images.path}")
    private String UPLOAD_DIR;

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final InCartRepository inCartRepository;

    public OrderService(UserRepository userRepository, OrderRepository orderRepository, InCartRepository inCartRepository) {

        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.inCartRepository = inCartRepository;
    }

    @Transactional
    public void closeCart(long userId) {
        User user = userRepository.findById(userId);
        Order order = new Order();
        //user.getOrders().add(order);
        //userRepository.save(user);
        order.setUser(user);
        order.setInOrder(user.getInCarts().stream().map().collect(Collectors.toSet()));
        orderRepository.save(order);
        inCartRepository.deleteAll(user.getInCarts());

    }
}
