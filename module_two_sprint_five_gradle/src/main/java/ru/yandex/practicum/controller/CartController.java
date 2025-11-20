package ru.yandex.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.dto.CartDto;
import ru.yandex.practicum.dto.ItemDto;

import ru.yandex.practicum.service.CartService;

@CrossOrigin(maxAge = 3600)
@Controller
@RequestMapping("/cart")
class CartController {

    private final CartService service;

    private static final String VIEWS_ITEMS_CART_FORM = "cart";
    private static final String VIEWS_ITEMS_ITEM_FORM = "item";

    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping("/items")
    public String getItems(	 Model model ){

        CartDto cartDto = service.findCardsItems(1L);
        model.addAttribute("items", cartDto.items());
        model.addAttribute("total", cartDto.total());
        return VIEWS_ITEMS_CART_FORM;
    }

    @PostMapping("/items")
    public String postItems(HttpServletRequest request,
                            @RequestParam(required = true) long id,
                            @RequestParam(required = true) String action,
                            Model model ){

        switch (action.toLowerCase()) {
            case "plus"     : service.changeInCardCount(id, 1); break;
            case "minus"    : service.changeInCardCount(id, 2); break;
            case "delete"   : service.changeInCardCount(id, 3); break;
            default		    : System.out.println("default");
        };
        CartDto cartDto = service.findCardsItems(1L);
        model.addAttribute("items", cartDto.items());
        model.addAttribute("total", cartDto.total());
        return VIEWS_ITEMS_CART_FORM;
    }

    @GetMapping(value={"/items/{id}"})
    public String getItem(@PathVariable(name = "id") Long id, Model model){
        ItemDto itemDto = service.findById(id);
        model.addAttribute("item", itemDto);
        return VIEWS_ITEMS_ITEM_FORM;
    }

    @PostMapping(value={"/items/{id}"})
    public String controlItem(@PathVariable(name = "id") Long id, @RequestParam(required = true) String action, Model model){
        ItemDto itemDto = service.findById(id);
        model.addAttribute("item", itemDto);
        return VIEWS_ITEMS_ITEM_FORM;
    }
}
