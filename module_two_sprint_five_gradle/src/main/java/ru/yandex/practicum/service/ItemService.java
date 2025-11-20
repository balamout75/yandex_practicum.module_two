package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ItemDto;

import ru.yandex.practicum.mapping.ItemEntityMapper;

import ru.yandex.practicum.model.InCart;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.InCartRepository;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.UserRepository;

@Service
public class ItemService {

    //@Value("${images.path}")
    private String UPLOAD_DIR;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final InCartService inCartService;
    private final OrderService orderService;

    private final ItemEntityMapper itemEntityMapper;

    public ItemService(UserRepository userRepository, ItemRepository itemRepository, InCartService inCartService, OrderService orderService, ItemEntityMapper itemEntityMapper) {
        this.itemRepository = itemRepository;
        this.inCartService = inCartService;
        this.orderService = orderService;
        this.itemEntityMapper = itemEntityMapper;
        this.userRepository = userRepository;
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

    public void changeInCardCount(long id, boolean b) {
        inCartService.changeInCardCount(id, (b)?1:2);
    }

    public void closeCart(long userId) {
        orderService.closeCart(userId);
    }
}
