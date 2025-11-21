package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.CartDto;
import ru.yandex.practicum.dto.ItemDto;

import ru.yandex.practicum.mapping.ItemToDtoMapper;

import ru.yandex.practicum.model.InCart;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.InCartRepository;
import ru.yandex.practicum.repository.UserRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CartService {


    //private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final InCartRepository inCartRepository;
    private final ItemToDtoMapper itemToDtoMapper;

    public CartService(UserRepository userRepository, InCartRepository inCartRepository, ItemToDtoMapper itemToDtoMapper) {
        this.inCartRepository = inCartRepository;
        this.itemToDtoMapper = itemToDtoMapper;
        this.userRepository = userRepository;
    }

    public void changeInCardCount(User user, Item item, int command) {
        InCart inCart = user.getInCarts().stream()
                .filter(u -> u.getItem().equals(item))
                .findFirst().orElse(null);
        switch (command) {
            case 1: {
                if (inCart ==null) { inCart =new InCart();
                    inCart.setItem(item);
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
        User user = userRepository.findById(userId);
        AtomicLong cartTotal= new AtomicLong();
        List <ItemDto> items =user.getInCarts().stream()
                .map(InCart::getItem)
                .map(u -> itemToDtoMapper.toDto(user,u))
                .peek(u -> cartTotal.set(cartTotal.get() + u.count() * u.price()))
                .toList();
        return new CartDto(items,cartTotal.get());
    }

}
