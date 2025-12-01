package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.CartDto;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapping.ActionModes;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.ItemService;

@CrossOrigin(maxAge = 3600)
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
    public String getItems(	 Model model ){

        CartDto cartDto = cartService.getCart(USER_ID);
        model.addAttribute("items", cartDto.items());
        model.addAttribute("total", cartDto.total());
        return VIEWS_ITEMS_CART_FORM;
    }

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
    }
}
