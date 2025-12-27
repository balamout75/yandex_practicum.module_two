package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.payment.ResultStatus;
import ru.yandex.practicum.dto.shoping.ItemsRequest;
import ru.yandex.practicum.security.CurrentUserId;
import ru.yandex.practicum.service.payment.PaymentService;
import ru.yandex.practicum.service.shoping.OrderService;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Controller
@RequestMapping()
class UserController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    UserController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @GetMapping("/login")
    public String login() {
        // Шаблон: login.html
        return "login";
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
    public Mono<Rendering> buyCart(@CurrentUserId Long userId) {
        return paymentService.buy()
                .flatMap(statusDto -> {
                    if (statusDto.status() == ResultStatus.ACCEPTED) {
                        return orderService.closeCart()
                                .flatMap(u -> Mono.just(Rendering.redirectTo("/orders/{id}?newOrder=true")
                                        .modelAttribute("id", u)
                                        .build()));
                    } else {
                        log.warn("Payment rejected: userId={}, total={}, status={}", userId, statusDto.total(), statusDto.status());
                        return Mono.just(Rendering.redirectTo("cart/items").build());
                    }
                });
    }
}
