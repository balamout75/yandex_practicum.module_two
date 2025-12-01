package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yandex.practicum.service.OrderService;

@CrossOrigin(maxAge = 3600)
@Controller
@RequestMapping()
class UserController {

    private final OrderService orderService;
    private static final long USER_ID = 1;

    public UserController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String getItems(	@RequestParam(defaultValue = "") String search,
                            @RequestParam(defaultValue = "NO") String sort,
                            @RequestParam(defaultValue = "1") int pageNumber,
                            @RequestParam(defaultValue = "5") int pageSize,
                            RedirectAttributes redirectAttributes ){
        redirectAttributes.addAttribute("search", search);
        redirectAttributes.addAttribute("sort", sort);
        redirectAttributes.addAttribute("pageNumber", pageNumber);
        redirectAttributes.addAttribute("pageSize", pageSize);
        return "redirect:/items?search={search}&sort={sort}&pageNumber={pageNumber}&pageSize={pageSize}";
    }

    @PostMapping(value={"/buy"})
    public String buyCart(RedirectAttributes redirectAttributes) {
        long orderId = orderService.closeCart(USER_ID);
        redirectAttributes.addAttribute("id", orderId);
        return "redirect:/orders/{id}?newOrder=true";
    }

}
