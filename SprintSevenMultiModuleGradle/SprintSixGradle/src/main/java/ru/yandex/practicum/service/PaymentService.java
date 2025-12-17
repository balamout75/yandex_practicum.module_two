package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.paymentclient.PaymentApi;
import ru.yandex.practicum.model.PaymentBalance;
import ru.yandex.practicum.model.PaymentOrder;
import ru.yandex.practicum.model.PaymentStatus;


@Service
public class PaymentService {

    private final PaymentApi paymentApi;

    public PaymentService(PaymentApi paymentApi) {
        this.paymentApi = paymentApi;
    }

    public Mono<PaymentBalance> getBalance(Long userId) {
        return paymentApi.paymentUserIdBalanceGet(userId)
                .flatMap(response -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        return Mono.just(response.getBody());
                    }
                    if (response.getStatusCode().value() == 404) {
                        return Mono.error(
                                new IllegalStateException("Пользователь не зарегистрирован")
                        );
                    }
                    return Mono.error(
                            new IllegalStateException("Payment service error")
                    );
                });
    }

    public Mono<PaymentBalance> getStatus(Long userId) {
        return paymentApi.paymentUserIdBalanceGet(userId)
                .flatMap(response -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        return Mono.just(response.getBody());
                    }
                    if (response.getStatusCode().value() == 404) {
                        return Mono.error(
                                new IllegalStateException("Пользователь не зарегистрирован")
                        );
                    }
                    return Mono.error(
                            new IllegalStateException("Payment service error")
                    );
                });
    }
    public Mono<PaymentStatus> buy(Long userId, PaymentOrder paymentOrder) {
        return paymentApi.paymentUserIdBuyPost(userId, paymentOrder)
                .flatMap(response -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        return Mono.just(response.getBody());
                    }
                    return Mono.error(new RuntimeException("Buy failed"));
                });
    }
}