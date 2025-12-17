package ru.yandex.practicum.paymentclient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.paymentclient.model.Balance;
import ru.yandex.practicum.paymentclient.model.Order;
import ru.yandex.practicum.paymentclient.model.Status;

@HttpExchange("/payment")
public interface PaymentApi {

    @GetExchange("/payment/{userId}/balance")
    Mono<ResponseEntity<Balance>> paymentUserIdBalanceGet(
            @PathVariable Long userId
    );

    @PostExchange("/payment/{userId}/buy")
    Mono<ResponseEntity<Status>> paymentUserIdBuyPost(
            @PathVariable Long userId,
            @RequestBody Order order
    );
}
