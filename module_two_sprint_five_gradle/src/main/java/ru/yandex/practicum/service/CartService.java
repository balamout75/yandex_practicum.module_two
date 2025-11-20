package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.CartDto;
import ru.yandex.practicum.dto.ItemDto;

import ru.yandex.practicum.mapping.ItemEntityMapper;

import ru.yandex.practicum.model.InCart;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.UserRepository;

import java.util.*;

@Service
public class CartService {

    //@Value("${images.path}")
    private String UPLOAD_DIR;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final InCartService inCartService;

    private final ItemEntityMapper itemEntityMapper;

    public CartService(UserRepository userRepository, ItemRepository itemRepository, InCartService inCartService, ItemEntityMapper itemEntityMapper) {
        this.itemRepository = itemRepository;
        this.inCartService = inCartService;
        this.itemEntityMapper = itemEntityMapper;
        this.userRepository = userRepository;
    }

    public CartDto findCardsItems(long userId) {
        long total = userRepository.findById(userId).getInCarts().stream()
                .mapToLong(u -> u.getCount()*u.getItem().getPrice())
                .sum();
        List <ItemDto> items =userRepository.findById(userId).getInCarts().stream()
                .map(InCart::getItem)
                .map(itemEntityMapper::toDto)
                .toList();
        return new CartDto(items,total);
    }

    public ItemDto findById(long id) {
        return itemRepository.findById((int)id).map(itemEntityMapper::toDto).orElse(null);
    }

    public void changeInCardCount(long id, int command) {
        inCartService.changeInCardCount(id, command);
    }
}
