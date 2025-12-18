package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.payment.BalanceDto;
import ru.yandex.practicum.dto.payment.BalanceStatus;
import ru.yandex.practicum.dto.shoping.CartRequest;

import ru.yandex.practicum.dto.shoping.ItemDto;
import ru.yandex.practicum.model.shoping.CartItem;
import ru.yandex.practicum.service.payment.PaymentService;
import ru.yandex.practicum.service.shoping.CartItemService;

import java.util.List;

import static reactor.netty.http.HttpConnectionLiveness.log;


@Controller
@RequestMapping("/cart")
class CartController {


    private final CartItemService cartItemService;
    private final PaymentService paymentService;
    private static final String VIEWS_ITEMS_CART_FORM = "cart";
    private static final long USER_ID = 1;

    public CartController(CartItemService cartItemService, PaymentService paymentService) {
        this.cartItemService = cartItemService;
        this.paymentService = paymentService;
    }

    /*
    @GetMapping("/items")
    public Mono<Rendering> getItems() {
        return cartItemService.getCart(USER_ID).collectList().zipWith(cartItemService.getCartCount(USER_ID))
                .map(u -> Rendering.view(VIEWS_ITEMS_CART_FORM)
                        .modelAttribute("items", u.getT1())
                        .modelAttribute("total", u.getT2()).build())
                .switchIfEmpty(Mono.just(Rendering.redirectTo("redirect:/items").build()));
    }

     */

    @GetMapping("/items")
    public Mono<Rendering> getItems() {

        Mono<List<ItemDto>> itemsMono = cartItemService.getCart(USER_ID).collectList();
        Mono<Long> totalMono = cartItemService.getCartCount(USER_ID);
        Mono<BalanceDto> balanceMono =  paymentService.getBalance(USER_ID);

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
        return cartItemService.changeInCardCount(USER_ID, itemsRequest.id(), itemsRequest.action())
                .thenReturn("redirect:/cart/items");
    }
}
