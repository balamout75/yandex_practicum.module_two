package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.CartDto;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapper.ActionModes;
//import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.ItemService;

@Controller
@RequestMapping("/cart")
class CartController {

    //private final UserService userService;
    private final CartService cartService;
    private final ItemService itemService;
    private static final String VIEWS_ITEMS_CART_FORM = "cart";
    private static final String VIEWS_ITEMS_ITEM_FORM = "item";
    private static final long USER_ID = 1;

    public CartController(CartService cartService, ItemService itemService) {
        this.cartService = cartService;
        this.itemService = itemService;
    }

    @GetMapping("/items")
    public Mono<Rendering> getItems(){
        return cartService.getCart(USER_ID).collectList().zipWith(cartService.getCartCount(USER_ID))
                .map(u -> Rendering.view(VIEWS_ITEMS_CART_FORM)
                        .modelAttribute("items", u.getT1())
                        .modelAttribute("total", u.getT2())
                        .build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo("not-found").build()));
    }

     /*

    @PostMapping("/items")
    public String postItems(@RequestParam(name = "id") long itemId,
                            @RequestParam() ActionModes action,
                            Model model ){

        cartService.changeInCardCount(USER_ID, itemId, action);
        CartDto cartDto = cartService.getCart(USER_ID);
        model.addAttribute("items", cartDto.items());
        model.addAttribute("total", cartDto.total());
        return VIEWS_ITEMS_CART_FORM;
    }

    @GetMapping(value={"/items/{id}"})
    public String getItem(@PathVariable(name = "id") Long itemId, Model model){
        ItemDto itemDto = itemService.findItem(USER_ID, itemId);
        model.addAttribute("item", itemDto);
        return VIEWS_ITEMS_ITEM_FORM;
    }

    @PostMapping(value={"/items/{id}"})
    public String controlItem(@PathVariable(name = "id") Long id, @RequestParam String action, Model model){
        ItemDto itemDto = itemService.findItem(USER_ID, id);
        model.addAttribute("item", itemDto);
        return VIEWS_ITEMS_ITEM_FORM;
    }*/
}
