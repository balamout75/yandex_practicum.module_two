package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapping.ItemToDtoMapper;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.ItemRepository;

import java.util.Optional;

@Service
public class ItemService {

    private final UserService       userService;
    private final ItemRepository    itemRepository;
    private final ItemToDtoMapper   itemToDtoMapper;


    public ItemService(UserService userService, ItemRepository itemRepository, ItemToDtoMapper itemToDtoMapper) {
        this.userService        = userService;
        this.itemRepository     = itemRepository;
        this.itemToDtoMapper    = itemToDtoMapper;
    }

    public Page<ItemDto> findAll(long userId, String search, Pageable pageable) {
        search = search.trim();
        User user = userService.getUser(userId);
        if (search.isBlank()) { return itemRepository.findAll(pageable).map(u -> itemToDtoMapper.toDto(user,u)) ;}
        else { return itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase
                                        (search,search,pageable).map(u -> itemToDtoMapper.toDto(user,u)) ; }
    }

    public ItemDto findItem(long userId, long itemId) {
        User user = userService.getUser(userId);
        Optional <Item> item =  itemRepository.findById(itemId);
        return item.map(value -> itemToDtoMapper.toDto(user, value)).orElse(null);
    }

    public Optional <Item> getItem(Long itemId) {
        return itemRepository.findById(itemId);
    }

    public boolean exists(long userId, Long itemId) {
        User user = userService.getUser(userId);
        return itemRepository.existsById(itemId);
    }
}
