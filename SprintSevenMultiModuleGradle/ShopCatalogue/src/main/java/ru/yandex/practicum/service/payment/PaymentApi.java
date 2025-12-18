package ru.yandex.practicum.service.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.payment.PaymentBalance;
import ru.yandex.practicum.model.payment.PaymentOrder;
import ru.yandex.practicum.model.payment.PaymentStatus;

@HttpExchange("/payment")
public interface PaymentApi {

    @GetExchange("/{userId}/balance")
    Mono<ResponseEntity<PaymentBalance>> paymentUserIdBalanceGet(
            @PathVariable Long userId
    );

    @PostExchange("/{userId}/buy")
    Mono<ResponseEntity<PaymentStatus>> paymentUserIdBuyPost(
            @PathVariable Long userId,
            @RequestBody PaymentOrder order
    );
}
