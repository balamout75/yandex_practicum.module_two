package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.paymentclient.PaymentApi;
import ru.yandex.practicum.paymentclient.model.Balance;
import ru.yandex.practicum.paymentclient.model.Order;
import ru.yandex.practicum.paymentclient.model.Status;


@Service
public class PaymentService {

    private final PaymentApi paymentApi;

    public PaymentService(PaymentApi paymentApi) {
        this.paymentApi = paymentApi;
    }

    public Mono<Balance> getBalance(Long userId) {
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

    public Mono<Balance> getStatus(Long userId) {
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
    public Mono<Status> buy(Long userId, Order order) {
        return paymentApi.paymentUserIdBuyPost(userId, order)
                .flatMap(response -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        return Mono.just(response.getBody());
                    }
                    return Mono.error(new RuntimeException("Buy failed"));
                });
    }
}