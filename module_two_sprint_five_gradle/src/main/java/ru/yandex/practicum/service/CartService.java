package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.CartDto;
import ru.yandex.practicum.dto.ItemDto;

import ru.yandex.practicum.mapping.ItemEntityMapper;

import ru.yandex.practicum.model.InCart;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.InCartRepository;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.UserRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CartService {


    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final InCartRepository inCartRepository;
    private final ItemEntityMapper itemEntityMapper;

    public CartService(UserRepository userRepository, ItemRepository itemRepository, InCartRepository inCartRepository, ItemEntityMapper itemEntityMapper) {
        this.itemRepository = itemRepository;
        this.inCartRepository = inCartRepository;
        this.itemEntityMapper = itemEntityMapper;
        this.userRepository = userRepository;
    }

    public ItemDto findById(long id) {
        return itemRepository.findById((int)id).map(itemEntityMapper::toDto).orElse(null);
    }

    public void changeInCardCount(long userId, long itemId, int command) {
        User user = userRepository.findById(userId);
        AtomicReference<Item> item = new AtomicReference<>();
        InCart inCart = user.getInCarts().stream()
                .filter(u -> u.getItem().getId() == itemId)
                .peek(u -> item.set(u.getItem()))
                .findFirst().orElse(null);
        switch (command) {
            case 1: {
                if (inCart ==null) { inCart =new InCart();
                    inCart.setItem(item.get());
                    inCart.setUser(user);
                    inCart.setCount(0l);
                    inCartRepository.save(inCart);
                }
                inCart.setCount(inCart.getCount()+1);
                inCartRepository.save(inCart);
                break;
            }
            case 2: {
                if ((inCart !=null)&&(inCart.getCount()>0)) {
                    inCart.setCount(inCart.getCount()-1);
                    inCartRepository.save(inCart);
                    if (inCart.getCount()==0)  { inCartRepository.delete(inCart); }
                }
                break;
            }
            case 3: {
                if (inCart !=null) { inCartRepository.delete(inCart); }
            }
        }
    }

    public CartDto getCart(long userId) {
        AtomicLong cartTotal= new AtomicLong();
        List <ItemDto> items =userRepository.findById(userId).getInCarts().stream()
                .map(InCart::getItem)
                .map(itemEntityMapper::toDto)
                .peek(u -> cartTotal.set(cartTotal.get() + u.count() * u.price()))
                .toList();
        return new CartDto(items,cartTotal.get());
    }
}
