package ru.yandex.practicum.server.service;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.model.PaymentBalance;
import ru.yandex.practicum.server.model.PaymentOrder;
import ru.yandex.practicum.server.model.PaymentStatus;

public interface PaymentValidationService {
    Mono<Boolean> checkUser(Long userId);
    Mono <PaymentBalance> getBalance(Long userId);
    Mono<PaymentStatus> getStatus(PaymentOrder paymentOrder);

}
