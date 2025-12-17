package ru.yandex.practicum.controller;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.shoping.CartRequest;
import ru.yandex.practicum.dto.shoping.ItemDto;
import ru.yandex.practicum.dto.shoping.ItemsRequest;
import ru.yandex.practicum.dto.shoping.Paging;
import ru.yandex.practicum.mapper.SortModes;
import ru.yandex.practicum.service.shoping.CartItemService;
import ru.yandex.practicum.service.shoping.ChartService;

@Controller
@RequestMapping("/items")
public class ItemController {

    private static final String VIEWS_ITEMS_CHART_FORM = "items";
    private static final String VIEWS_ITEMS_ITEM_FORM = "item";
    private static final long USER_ID = 1;
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    private final ChartService chartService;
    private final CartItemService cartItemService;

    public ItemController(ChartService chartService, CartItemService cartItemService) {
        this.chartService = chartService;
        this.cartItemService = cartItemService;
    }

    @GetMapping
    public Mono<Rendering> list(@ModelAttribute ItemsRequest itemsRequest) {
        Sort sortmode = switch (itemsRequest.getSort()) {
            case SortModes.PRICE    -> Sort.by(Sort.Direction.ASC, "price");
            case SortModes.ALPHA    -> Sort.by(Sort.Direction.ASC, "title");
            default                 -> Sort.by(Sort.Direction.ASC, "id");
        };
        Pageable pageable = PageRequest.of(itemsRequest.getPageNumber() - 1, itemsRequest.getPageSize(), sortmode);
        return chartService.findAll(USER_ID, itemsRequest.getSearch(), pageable).collectList().map(items -> {
            while ((items.size() % 3) != 0) {
                items.add(new ItemDto());
            }
            return Lists.partition(items, 3);
        }).zipWith(chartService.count())
            .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()))
            .map(u -> Rendering.view(VIEWS_ITEMS_CHART_FORM)
                .modelAttribute("items", u)
                .modelAttribute("search", itemsRequest.getSearch())
                .modelAttribute("sort", itemsRequest.getSort().toString())
                .modelAttribute("paging", new Paging(itemsRequest.getPageSize(),
                        itemsRequest.getPageNumber(),
                        u.hasPrevious(),
                        u.hasNext()))
                .build());
    }


    @GetMapping(value = {"/{id}"})
    public Mono<Rendering> getItem(@PathVariable(name = "id") Long itemId) {
        return chartService.findItem(USER_ID, itemId)
                .map(u -> Rendering.view(VIEWS_ITEMS_ITEM_FORM)
                        .modelAttribute("item", u)
                        .build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo("/items").build()));
    }

    @PostMapping()
    public Mono<String> postItems(@ModelAttribute ItemsRequest itemsRequest, Model model) {
        model.addAttribute("search", itemsRequest.getSearch());
        model.addAttribute("sort", itemsRequest.getSort());
        model.addAttribute("pageNumber", itemsRequest.getPageNumber());
        model.addAttribute("pageSize", itemsRequest.getPageSize());
        return cartItemService.changeInCardCount(USER_ID, itemsRequest.getId(), itemsRequest.getAction())
                .thenReturn("redirect:/items?search={search}&sort={sort}&pageNumber={pageNumber}&pageSize={pageSize}");
    }

    @PostMapping(value = {"/{id}"})
    public Mono<String> postItem(@ModelAttribute CartRequest cartRequest, Model model) {
        model.addAttribute("id", cartRequest.id());
        return cartItemService.changeInCardCount(USER_ID, cartRequest.id(), cartRequest.action())
                .thenReturn("redirect:/items/{id}");
    }


}