package ru.yandex.practicum.controller;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.payment.BalanceDto;
import ru.yandex.practicum.dto.payment.BalanceStatus;
import ru.yandex.practicum.dto.shoping.CartRequest;
import ru.yandex.practicum.dto.shoping.ItemDto;
import ru.yandex.practicum.security.CurrentUserId;
import ru.yandex.practicum.service.payment.PaymentService;
import ru.yandex.practicum.service.shoping.CartItemService;
import java.util.List;


@Controller
@RequestMapping("/cart")
class CartController {


    private final CartItemService cartItemService;
    private final PaymentService paymentService;
    private static final String VIEWS_ITEMS_CART_FORM = "cart";

    public CartController(CartItemService cartItemService, PaymentService paymentService) {
        this.cartItemService = cartItemService;
        this.paymentService = paymentService;
    }

    @GetMapping("/items")
    public Mono<Rendering> getItems() {

        Mono<List<ItemDto>> itemsMono = cartItemService.getCart().collectList();
        Mono<Long> totalMono = cartItemService.getCartTotal();
        Mono<BalanceDto> balanceMono =  paymentService.getBalance();

        return Mono.zip(itemsMono, totalMono, balanceMono)
                .filter(tuple -> !tuple.getT1().isEmpty())
                .map(tuple -> {
                    List<ItemDto> items = tuple.getT1();
                    long total = tuple.getT2();
                    BalanceDto balance = tuple.getT3();

                    boolean available = true;
                    String unavailableReason = null;

                    if (balance.status() != BalanceStatus.ACCEPTED) {
                        available = false;
                        unavailableReason = "Платёж временно недоступен";
                    } else if (balance.balance() < total) {
                        available = false;
                        unavailableReason = "Недостаточно средств";
                    }

                    return Rendering.view(VIEWS_ITEMS_CART_FORM)
                            .modelAttribute("items", items)
                            .modelAttribute("total", total)
                            .modelAttribute("available", available)
                            .modelAttribute("unavailableReason", unavailableReason)
                            .build();
                })

                .switchIfEmpty(Mono.just(Rendering.redirectTo("/items").build()));
    }

    @PostMapping("/items")
    public Mono<String> postItems(@ModelAttribute CartRequest itemsRequest) {
        return cartItemService.changeInCardCount(itemsRequest.id(), itemsRequest.action())
                .thenReturn("redirect:/cart/items");
    }
}
