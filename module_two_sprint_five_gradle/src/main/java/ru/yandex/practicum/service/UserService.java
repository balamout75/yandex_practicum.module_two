package ru.yandex.practicum.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.UserRepository;
import java.util.List;

@Lazy
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
        return userRepository.findById(userId);
    }

    public void changeInCardCount(long userId, long itemId, int i) {
        itemService.changeInCardCount(getUser(userId), itemId, i);
    }

    public Page<ItemDto> findAll(long userId, String search, Pageable pageable) {
        return itemService.findAll(getUser(userId), search, pageable);
    }

    public ItemDto findItem(long userId, Long itemId) {
        return itemService.findItem(getUser(userId), itemId);
    }
}
