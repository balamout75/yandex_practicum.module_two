package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ItemDto;

import ru.yandex.practicum.mapping.ItemEntityMapper;

import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.repository.ItemRepository;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CartService cartService;
    private final ItemEntityMapper itemEntityMapper;

    public ItemService(ItemRepository itemRepository, CartService cartService, ItemEntityMapper itemEntityMapper) {
        this.itemRepository     = itemRepository;
        this.cartService        = cartService;
        this.itemEntityMapper   = itemEntityMapper;
    }

    public Page<ItemDto> findAll(String search, Pageable pageable) {
        int searchCondition=0;
        Page<Item>  postEntities = switch (searchCondition) {
            case 1  -> itemRepository.findAll(pageable);
            default -> itemRepository.findAll(pageable);
        };
        return postEntities.map(itemEntityMapper::toDto);
    }

    public ItemDto findById(long id) {
        return itemRepository.findById((int)id).map(itemEntityMapper::toDto).orElse(null);
    }

    public void changeInCardCount(long userId, long itemId, boolean command) {
        cartService.changeInCardCount(userId, itemId,  (command)?1:2);
    }

}
