package ru.yandex.practicum.controller;

import com.google.common.collect.Lists;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.shoping.*;
import ru.yandex.practicum.mapper.SortModes;
import ru.yandex.practicum.security.CurrentUserId;
import ru.yandex.practicum.service.shoping.CartItemService;
import ru.yandex.practicum.service.shoping.CatalogueService;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Controller
@RequestMapping("/items")
public class CatalogueController {

    private static final String VIEWS_ITEMS_CHART_FORM = "items";
    private static final String VIEWS_ITEMS_ITEM_FORM = "item";
    private final CatalogueService catalogueService;
    private final CartItemService cartItemService;

    public CatalogueController(CatalogueService catalogueService, CartItemService cartItemService) {
        this.catalogueService = catalogueService;
        this.cartItemService = cartItemService;
    }

    @GetMapping("/debug")
    public Mono<String> debug() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getPrincipal())
                .doOnNext(principal ->
                        log.info("Principal class: {}", principal.getClass().getName())
                )
                .thenReturn("Ок");
    }

    @GetMapping
    public Mono<Rendering> list(@CurrentUserId Long userId,
                                @ModelAttribute ItemsRequest itemsRequest) {
        boolean authorized = userId != null && userId > 0;
        Sort sortmode = switch (itemsRequest.getSort()) {
            case SortModes.PRICE    -> Sort.by(Sort.Direction.ASC, "price");
            case SortModes.ALPHA    -> Sort.by(Sort.Direction.ASC, "title");
            default                 -> Sort.by(Sort.Direction.ASC, "id");
        };
        Pageable pageable = PageRequest.of(itemsRequest.getPageNumber() - 1, itemsRequest.getPageSize(), sortmode);
        return catalogueService.findAll(itemsRequest.getSearch(), pageable).collectList().map(items -> {
            while ((items.size() % 3) != 0) {
                items.add(new ItemDto());
            }
            return Lists.partition(items, 3);
        }).zipWith(catalogueService.count())
            .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()))
            .map(u -> Rendering.view(VIEWS_ITEMS_CHART_FORM)
                .modelAttribute("items", u)
                .modelAttribute("search", itemsRequest.getSearch())
                .modelAttribute("sort", itemsRequest.getSort().toString())
                .modelAttribute("paging", new Paging(itemsRequest.getPageSize(), itemsRequest.getPageNumber(), u.hasPrevious(), u.hasNext()))
                .modelAttribute("authorized", authorized)
                .build());
    }


    @GetMapping(value = {"/{id}"})
    public Mono<Rendering> getItem(@CurrentUserId Long userId, @PathVariable(name = "id") Long itemId) {
        boolean authorized = userId != null && userId > 0;
        return catalogueService.findItem(itemId)
                .map(u -> Rendering.view(VIEWS_ITEMS_ITEM_FORM)
                        .modelAttribute("item", u)
                        .modelAttribute("authorized", authorized)
                        .build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo("/items").build()));
    }

    @PostMapping()
    public Mono<String> postItems(@ModelAttribute ItemsRequest itemsRequest, Model model) {
        model.addAttribute("search", itemsRequest.getSearch());
        model.addAttribute("sort", itemsRequest.getSort());
        model.addAttribute("pageNumber", itemsRequest.getPageNumber());
        model.addAttribute("pageSize", itemsRequest.getPageSize());
        return cartItemService.changeInCardCount(itemsRequest.getId(), itemsRequest.getAction())
                .thenReturn("redirect:/items?search={search}&sort={sort}&pageNumber={pageNumber}&pageSize={pageSize}");
    }

    @PostMapping(value = {"/{id}"})
    public Mono<String> postItem(@ModelAttribute CartRequest cartRequest, Model model) {
        model.addAttribute("id", cartRequest.id());
        return cartItemService.changeInCardCount(cartRequest.id(), cartRequest.action())
                .thenReturn("redirect:/items/{id}");
    }
}