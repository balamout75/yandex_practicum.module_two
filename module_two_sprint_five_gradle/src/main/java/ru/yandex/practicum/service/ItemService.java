package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapping.ActionModes;
import ru.yandex.practicum.mapping.ItemToDtoMapper;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.ItemRepository;

import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository    itemRepository;
    private final CartService       cartService;
    private final ItemToDtoMapper   itemToDtoMapper;


    public ItemService(ItemRepository itemRepository, CartService cartService, ItemToDtoMapper itemToDtoMapper) {
        this.itemRepository     = itemRepository;
        this.cartService        = cartService;
        this.itemToDtoMapper = itemToDtoMapper;
    }

    public Page<ItemDto> findAll(User user, String search, Pageable pageable) {
        if (search.isBlank()) { return itemRepository.findAll(pageable).map(u -> itemToDtoMapper.toDto(user,u)) ;}
        else { return itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase
                                        (search,search,pageable).map(u -> itemToDtoMapper.toDto(user,u)) ; }
    }

    public ItemDto findItem(User user, long itemId) {
        Optional <Item> item =  itemRepository.findById(itemId);
        return item.map(value -> itemToDtoMapper.toDto(user, value)).orElse(null);
    }

    public Optional <Item> getItem(Long itemId) {
        System.out.println(itemRepository.existsById(itemId));
        return itemRepository.findById(itemId);
    }

    public void changeInCardCount(User user, long itemId, ActionModes action) {
        cartService.changeInCardCount(user, getItem(itemId), action);
    }

    public boolean exists(User user, Long itemId) {
        return itemRepository.existsById(itemId);
    }
}
