package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.CartRequest;

import ru.yandex.practicum.service.CartItemService;


@Controller
@RequestMapping("/cart")
class CartController {


    private final CartItemService cartItemService;
    private static final String VIEWS_ITEMS_CART_FORM = "cart";
    private static final long USER_ID = 1;

    public CartController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping("/items")
    public Mono<Rendering> getItems() {
        return cartItemService.getCart(USER_ID).collectList().zipWith(cartItemService.getCartCount(USER_ID))
                .map(u -> Rendering.view(VIEWS_ITEMS_CART_FORM)
                        .modelAttribute("items", u.getT1())
                        .modelAttribute("total", u.getT2()).build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo("redirect:/items").build()));
    }

    @PostMapping("/items")
    public Mono<String> postItems(@ModelAttribute CartRequest itemsRequest) {
        return cartItemService.changeInCardCount(USER_ID, itemsRequest.id(), itemsRequest.action())
                .thenReturn("redirect:/cart/items");
    }
}
