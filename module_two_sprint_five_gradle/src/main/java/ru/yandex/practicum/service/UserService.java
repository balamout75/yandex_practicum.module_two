package ru.yandex.practicum.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.mapping.ActionModes;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.UserRepository;
import java.util.List;

@Service
public class UserService {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final ItemService itemService;

    public UserService(OrderService orderService, UserRepository userRepository, ItemService itemService) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.itemService = itemService;
    }

    public List<OrderDto> findOrders(long userId)  {
        return orderService.findOrders(getUser(userId));
    }

    public long closeCart(long userId) {
        return orderService.closeCart(getUser(userId));
    }
    public User getUser(long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public void changeInCardCount(long userId, long itemId, ActionModes action) {
        itemService.changeInCardCount(getUser(userId), itemId, action);
    }

    public Page<ItemDto> findAll(long userId, String search, Pageable pageable) {
        return itemService.findAll(getUser(userId), search.trim(), pageable);
    }

    public ItemDto findItem(Long userId, Long itemId) {
        return itemService.findItem(getUser(userId), itemId);
    }

    public boolean existsItem(Long userId, Long itemId) {
        return itemService.exists(getUser(userId), itemId);
    }

    public boolean existsOrder(Long userId, Long orderId) {
        return orderService.exists(getUser(userId), orderId);
    }

    public OrderDto findOrder(long userId, Long orderId) {
        return orderService.findOrder(getUser(userId), orderId);
    }
}
