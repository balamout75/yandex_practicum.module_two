package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.service.OrderService;
//import ru.yandex.practicum.service.OrderService;

@Controller
@RequestMapping()
class UserController {

    //private final OrderService orderService;
    private static final long USER_ID = 1;
    private final OrderService orderService;

    public UserController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/")
    public Mono<Rendering> getItems(@RequestParam(defaultValue = "") String search,
                                    @RequestParam(defaultValue = "NO") String sort,
                                    @RequestParam(defaultValue = "1") int pageNumber,
                                    @RequestParam(defaultValue = "5") int pageSize) {
        return Mono.just(Rendering.redirectTo("/items")
                        .modelAttribute("search", search)
                        .modelAttribute("sort", sort)
                        .modelAttribute("pageNumber", pageNumber)
                        .modelAttribute("pageSize", pageSize)
                        .build());
    }

    @PostMapping(value={"/buy"})
    public Mono<Rendering> buyCart(Model model) {
        return orderService.closeCart(USER_ID)
                .flatMap(u -> Mono.just(Rendering.redirectTo("/orders/{id}?newOrder=true")
                        .modelAttribute("id", u)
                        .build()));
    }
}
