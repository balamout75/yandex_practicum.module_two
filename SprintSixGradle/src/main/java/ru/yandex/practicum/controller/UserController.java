package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemsRequest;
import ru.yandex.practicum.service.OrderService;

@Controller
@RequestMapping()
class UserController {

    private static final long USER_ID = 1;
    private final OrderService orderService;

    public UserController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/")
    public Mono<Rendering> getItems(@ModelAttribute ItemsRequest itemsRequest) {
        return Mono.just(Rendering.redirectTo("/items")
                .modelAttribute("search", itemsRequest.getSearch())
                .modelAttribute("sort", itemsRequest.getSort())
                .modelAttribute("pageNumber", itemsRequest.getPageNumber())
                .modelAttribute("pageSize", itemsRequest.getPageSize())
                .build());
    }

    @PostMapping(value = {"/buy"})
    public Mono<Rendering> buyCart(Model model) {
        return orderService.closeCart(USER_ID)
                .flatMap(u -> Mono.just(Rendering.redirectTo("/orders/{id}?newOrder=true")
                        .modelAttribute("id", u)
                        .build()));
    }
}
