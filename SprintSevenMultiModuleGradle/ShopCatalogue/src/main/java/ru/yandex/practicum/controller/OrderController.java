package ru.yandex.practicum.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.security.UserPrincipal;
import ru.yandex.practicum.service.shoping.OrderService;

@Controller
@RequestMapping("/orders")
class OrderController {

    private static final String VIEW_ORDERS = "orders";
    private static final String VIEW_ORDER = "order";
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping()
    public Mono<Rendering> getOrders(@AuthenticationPrincipal UserPrincipal user) {
        return orderService.findOrders(user.userId()).collectList()
                .map(u -> Rendering.view(VIEW_ORDERS)
                        .modelAttribute("orders", u)
                        .build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo("redirect:/items").build()));
    }

    @GetMapping("/{id}")
    public Mono<Rendering> getOrder(@AuthenticationPrincipal UserPrincipal user, @PathVariable(name = "id") Long orderId, @RequestParam(defaultValue = "false") String newOrder) {
        return orderService.findOrder(user.userId(), orderId)
                .map(u -> Rendering.view(VIEW_ORDER)
                        .modelAttribute("order", u)
                        .modelAttribute("newOrder", newOrder)
                        .build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo("redirect:/items").build()));
    }
}
