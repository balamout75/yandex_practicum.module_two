package ru.yandex.practicum.service;

import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.CartDto;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapping.ActionModes;
import ru.yandex.practicum.mapping.ItemToDtoMapper;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.InCartRepository;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.UserRepository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CartService {


    private final UserService userService;
    private final ItemService itemService;
    private final InCartRepository inCartRepository;
    private final ItemToDtoMapper itemToDtoMapper;

    public CartService(UserService userService, ItemService itemService, InCartRepository inCartRepository, ItemToDtoMapper itemToDtoMapper) {
        this.itemService = itemService;
        this.inCartRepository = inCartRepository;
        this.itemToDtoMapper = itemToDtoMapper;
        this.userService = userService;
    }

    public void changeInCardCount(Long userId, Long itemId, ActionModes action) {
        User user = userService.getUser(userId);
        Optional <Item> item = itemService.getItem(itemId);
        if (item.isPresent()) {
            CartItem cartItem = user.getInCarts().stream()
                    .filter(u -> u.getItem().isSimilar(item.get()))
                    .findFirst().orElse(null);
            switch (action) {
                case ActionModes.PLUS: {
                    if (cartItem == null) {
                        cartItem = new CartItem(user, item.get());
                    }
                    cartItem.countPlus();
                    inCartRepository.save(cartItem);
                    break;
                }
                case ActionModes.MINUS: {
                    if ((cartItem != null) && (cartItem.getCount() > 0)) {
                        cartItem.countMinus();
                        inCartRepository.save(cartItem);
                        if (cartItem.getCount() == 0) {
                            inCartRepository.delete(cartItem);
                        }
                    }
                    break;
                }
                case ActionModes.DELETE: {
                    if (cartItem != null) {
                        inCartRepository.delete(cartItem);
                    }
                }
            }
        }
    }

    public CartDto getCart(long userId) {
        User user = userService.getUser(userId);
        AtomicLong cartTotal= new AtomicLong();
        List <ItemDto> items =user.getInCarts().stream()
                .map(CartItem::getItem)
                .map(u -> itemToDtoMapper.toDto(user,u))
                .peek(u -> cartTotal.set(cartTotal.get() + u.count() * u.price()))
                .toList();
        return new CartDto(items,cartTotal.get());
    }

}
