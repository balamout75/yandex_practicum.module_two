package ru.yandex.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.CartDto;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.service.UserService;

@CrossOrigin(maxAge = 3600)
@Controller
@RequestMapping("/cart")
class CartController {

    private final UserService userService;
    private final CartService service;

    private static final String VIEWS_ITEMS_CART_FORM = "cart";
    private static final String VIEWS_ITEMS_ITEM_FORM = "item";
    private static final long USER_ID = 1;

    public CartController(UserService userService, CartService service) {
        this.userService = userService;
        this.service = service;
    }

    @GetMapping("/items")
    public String getItems(	 Model model ){

        CartDto cartDto = service.getCart(USER_ID);
        model.addAttribute("items", cartDto.items());
        model.addAttribute("total", cartDto.total());
        return VIEWS_ITEMS_CART_FORM;
    }

    @PostMapping("/items")
    public String postItems(@RequestParam(name = "id") long itemId,
                            @RequestParam String action,
                            Model model ){

        switch (action.toLowerCase()) {
            case "plus"     : userService.changeInCardCount(USER_ID, itemId, 1); break;
            case "minus"    : userService.changeInCardCount(USER_ID, itemId, 2); break;
            case "delete"   : userService.changeInCardCount(USER_ID, itemId, 3); break;
            default		    : System.out.println("default");
        }
        CartDto cartDto = service.getCart(USER_ID);
        model.addAttribute("items", cartDto.items());
        model.addAttribute("total", cartDto.total());
        return VIEWS_ITEMS_CART_FORM;
    }

    @GetMapping(value={"/items/{id}"})
    public String getItem(@PathVariable(name = "id") Long itemId, Model model){
        ItemDto itemDto = userService.findItem(USER_ID, itemId);
        model.addAttribute("item", itemDto);
        return VIEWS_ITEMS_ITEM_FORM;
    }

    @PostMapping(value={"/items/{id}"})
    public String controlItem(@PathVariable(name = "id") Long id, @RequestParam String action, Model model){
        ItemDto itemDto = userService.findItem(USER_ID, id);
        model.addAttribute("item", itemDto);
        return VIEWS_ITEMS_ITEM_FORM;
    }
}
