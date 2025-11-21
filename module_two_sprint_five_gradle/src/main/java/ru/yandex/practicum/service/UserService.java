package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final OrderService orderService;

    public UserService(OrderService orderService) {
        this.orderService = orderService;
    }
    public long closeCart(long userId) {
        return orderService.closeCart(userId);
    }
}
