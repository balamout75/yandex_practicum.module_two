package ru.yandex.practicum.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.payment.ResultStatus;
import ru.yandex.practicum.dto.shoping.ItemsRequest;
import ru.yandex.practicum.model.payment.PaymentOrder;
import ru.yandex.practicum.security.UserPrincipal;
import ru.yandex.practicum.service.payment.PaymentService;
import ru.yandex.practicum.service.shoping.CartItemService;
import ru.yandex.practicum.service.shoping.OrderService;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Controller
@RequestMapping()
class UserController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final CartItemService cartItemService;

    UserController(OrderService orderService, PaymentService paymentService, CartItemService cartItemService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.cartItemService = cartItemService;
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
    public Mono<Rendering> buyCart(@AuthenticationPrincipal UserPrincipal user, Model model) {
        return cartItemService.getCartCount(user.userId())
                            .zipWhen(total -> paymentService.buy(user.userId(), total))
                            .flatMap(tuple -> {
                                if (tuple.getT2().status() == ResultStatus.ACCEPTED) {
                                    return orderService.closeCart(user.userId())
                                            .flatMap(u -> Mono.just(Rendering.redirectTo("/orders/{id}?newOrder=true")
                                                    .modelAttribute("id", u)
                                                    .build()));
                                } else {
                                    log.warn("Payment rejected: userId={}, total={}, status={}", user.userId(), tuple.getT1(), tuple.getT2().status());
                                    return Mono.just(Rendering.redirectTo("cart/items").build());
                                }
                            });
    }
}
