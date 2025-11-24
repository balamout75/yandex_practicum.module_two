package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapping.ItemToDtoMapper;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.ItemRepository;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CartService    cartService;
    private final ItemToDtoMapper itemToDtoMapper;


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
        return itemToDtoMapper.toDto(user,itemRepository.findById(itemId));
    }

    public Item getItem(long itemId) {
        return itemRepository.findById(itemId);
    }

    public void changeInCardCount(User user, long itemId, int command) {
        cartService.changeInCardCount(user, getItem(itemId), command);
    }

}
