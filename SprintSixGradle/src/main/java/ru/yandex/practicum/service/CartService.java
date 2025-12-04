package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.CartDto;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapper.ActionModes;
import ru.yandex.practicum.mapper.ItemToDtoMapper;
//import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.CartItemRepository;
//import ru.yandex.practicum.repository.CartItemRepository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CartService {

    @Value("${images.path}")
    private String UPLOAD_DIR;

    private final CartItemRepository repository;

    public CartService(CartItemRepository repository) {
            this.repository = repository;
    }

    /*public void changeInCardCount(Long userId, Long itemId, ActionModes action) {
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
    }*/

    public Flux<ItemDto> getCart(long userId) {
        return repository.inCartItems(userId).map(u -> ItemToDtoMapper.toDto(u, UPLOAD_DIR));
    }

    public Mono<Long> getCartCount(long userId) {
        return repository.inCartCount(userId);
    }

}
