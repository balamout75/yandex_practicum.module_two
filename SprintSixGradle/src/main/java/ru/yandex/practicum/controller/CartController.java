package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.CartRequest;

import ru.yandex.practicum.service.CartService;


@Controller
@RequestMapping("/cart")
class CartController {


    private final CartService cartService;
    private static final String VIEWS_ITEMS_CART_FORM = "cart";
    private static final String VIEWS_ITEMS_ITEM_FORM = "item";
    private static final long USER_ID = 1;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/items")
    public Mono<Rendering> getItems(){
        return cartService.getCart(USER_ID).collectList().zipWith(cartService.getCartCount(USER_ID))
                .map(u -> Rendering.view(VIEWS_ITEMS_CART_FORM)
                        .modelAttribute("items", u.getT1())
                        .modelAttribute("total", u.getT2())
                        .build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo("redirect:/items").build()));
    }

    @PostMapping("/items")
    public Mono<String> postItems(@ModelAttribute CartRequest itemsRequest, Model model){
        return cartService.changeInCardCount(USER_ID, itemsRequest.id(), itemsRequest.action())
                .thenReturn("redirect:/cart/items");
    }
}
