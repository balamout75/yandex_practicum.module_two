package ru.yandex.practicum.server.controller;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.Generated;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.api.ApiUtil;
import ru.yandex.practicum.server.api.PaymentApi;
import ru.yandex.practicum.server.model.Balance;
import ru.yandex.practicum.server.model.Order;
import ru.yandex.practicum.server.model.Status;
import ru.yandex.practicum.server.service.PaymentService;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
@Controller
@RequestMapping("${openapi.shopPaymentService.base-path:}")
public class PaymentController implements PaymentApi {

    PaymentService paymentService;

    PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public Mono<ResponseEntity<Balance>> paymentUserIdBalanceGet(
            @NotNull @Parameter(name = "userId", description = "", required = true, in = ParameterIn.PATH) @PathVariable("userId") Long userId,
            @Parameter(hidden = true) final ServerWebExchange exchange
    ) {
        return paymentService.checkUser(userId)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Пользователь не зарегистрирован"
                        )
                ))
                .flatMap(v -> paymentService.getBalance(userId))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Сервис не доступен"
                        )
                ));
    }

    @Override
    public Mono<ResponseEntity<Status>> paymentUserIdBuyPost(
            @NotNull @Parameter(name = "userId", description = "", required = true, in = ParameterIn.PATH) @PathVariable("userId") Long userId,
            @Parameter(name = "Order", description = "", required = true) @Valid @RequestBody Mono<Order> order,
            @Parameter(hidden = true) final ServerWebExchange exchange
    ) {
        return paymentService.checkUser(userId)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Пользователь не зарегистрирован"
                        )
                ))
                .then(order)
                .flatMap(paymentService::getStatus)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Сервис не доступен"
                        )
                ));
    }
}
