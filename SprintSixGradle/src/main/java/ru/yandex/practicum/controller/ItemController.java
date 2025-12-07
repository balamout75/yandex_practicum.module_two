package ru.yandex.practicum.controller;

import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.dto.ItemsRequest;
import ru.yandex.practicum.dto.Paging;
import ru.yandex.practicum.mapper.SortModes;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.ItemService;

import java.util.Optional;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final CartService cartService;
    private static final String VIEWS_ITEMS_CHART_FORM = "items";
    private static final String VIEWS_ITEMS_ITEM_FORM = "item";
    private static final long 	USER_ID = 1;

    public ItemController(ItemService itemService, CartService cartService) {
        this.itemService = itemService;
        this.cartService = cartService;
    }

    @GetMapping
    public Mono<Rendering> list(@ModelAttribute ItemsRequest itemsRequest, Model model) {
        Sort sortmode = switch (itemsRequest.getSort()) {
            case SortModes.PRICE 	-> Sort.by(Sort.Direction.ASC, "price") ;
            case SortModes.ALPHA 	-> Sort.by(Sort.Direction.ASC, "title") ;
            default					-> Sort.by(Sort.Direction.ASC, "id");
        };
        log.info("класс проверили "+itemsRequest.toString());
        Pageable pageable = PageRequest.of(itemsRequest.getPageNumber()-1, itemsRequest.getPageSize(), sortmode);
        log.info("класс проверили "+pageable);
        return  itemService.findAll(USER_ID, itemsRequest.getSearch(),pageable).collectList()
                .map(items -> {
                        while ((items.size() % 3) !=0 ) { items.add(new ItemDto());}
                        return ListUtils.partition(items, 3);
                    }
                )
                .zipWith(itemService.count())
                .map(p -> { var c =new PageImpl<>(p.getT1(), pageable, p.getT2());
                                                 return c;
                                                })
                .map(u -> Rendering.view(VIEWS_ITEMS_CHART_FORM)
                                                    .modelAttribute("items",u)
                                                    .modelAttribute("search", itemsRequest.getSearch())
                                                    .modelAttribute("sort",   itemsRequest.getSort().toString())
                                                    .modelAttribute("paging", new Paging(itemsRequest.getPageSize(),
                                                                                               itemsRequest.getPageNumber(),
                                                                                               u.hasPrevious(),
                                                                                               u.hasNext()))
                                                    .build());
    }


    @GetMapping(value={"/{id}"})
    public Mono<Rendering> getItem(@PathVariable(name = "id") Long itemId) {
        return itemService.findItem(USER_ID, itemId)
                .map(u -> Rendering.view(VIEWS_ITEMS_ITEM_FORM)
                        .modelAttribute("item", u)
                        .build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo(VIEWS_ITEMS_CHART_FORM).build()));
    }

    @PostMapping()
    public Mono<String> postItems(@ModelAttribute ItemsRequest itemsRequest, Model model) {
        model.addAttribute("search",        itemsRequest.getSearch());
        model.addAttribute("sort",          itemsRequest.getSort());
        model.addAttribute("pageNumber",    itemsRequest.getPageNumber());
        model.addAttribute("pageSize",      itemsRequest.getPageSize());
        return cartService.changeInCardCount(USER_ID, itemsRequest.getId(), itemsRequest.getAction())
                .thenReturn("redirect:/items?search={search}&sort={sort}&pageNumber={pageNumber}&pageSize={pageSize}");
    }
}