package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.service.OrderService;

@Controller
@RequestMapping("/orders")
class OrderController {

    private static final String VIEW_ORDERS = "orders";
    private static final String VIEW_ORDER = "order";
    private static final long USER_ID = 1;
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping()
    public Mono<Rendering> getOrders() {
        return orderService.findOrders(USER_ID).collectList()
                .map(u -> Rendering.view(VIEW_ORDERS)
                        .modelAttribute("orders", u)
                        .build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo("not-found2").build()));
    }

    @GetMapping("/{id}")
    public Mono<Rendering> getOrder(@PathVariable(name = "id") Long orderId, @RequestParam(defaultValue = "false") String newOrder) {
        System.out.println("orderId: " + orderId);
        return orderService.findOrder(USER_ID, orderId)
                .map(u -> Rendering.view(VIEW_ORDER)
                        .modelAttribute("order", u)
                        .modelAttribute("newOrder", newOrder)
                        .build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo("not-found3").build()));
    }
}
