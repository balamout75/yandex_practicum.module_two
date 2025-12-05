package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.service.OrderService;

@Controller
@RequestMapping("/orders")
class OrderController {

    private final OrderService orderService;

    private static final String VIEW_ORDERS = "orders";
    private static final String VIEW_ORDER  = "order";
    private static final long USER_ID = 1;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping()
    public Mono<Rendering> getOrders( ){
        return orderService.findOrders(USER_ID)
                .map(u -> Rendering.view(VIEW_ORDERS)
                        .modelAttribute("orders", u)
                        .build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo("not-found").build()));
    }

    @GetMapping("/{id}")
    public Mono<Rendering> getOrder(@PathVariable(name = "id") Long orderId, @RequestParam(defaultValue = "false") String newOrder){
        return orderService.findOrder(USER_ID, orderId)
                .map(u -> Rendering.view(VIEW_ORDER)
                        .modelAttribute("order", u)
                        .modelAttribute("newOrder", newOrder)
                        .build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo("not-found").build()));
    }


}
