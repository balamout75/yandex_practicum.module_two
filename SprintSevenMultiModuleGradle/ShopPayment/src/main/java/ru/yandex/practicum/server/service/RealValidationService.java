package ru.yandex.practicum.server.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.model.PaymentBalance;
import ru.yandex.practicum.server.model.PaymentOrder;
import ru.yandex.practicum.server.model.PaymentStatus;

import java.util.Objects;

@Service
@ConditionalOnProperty(name = "paymentservice.game-mode", havingValue = "false")
public class RealValidationService implements PaymentValidationService {

    public RealValidationService() {    }
    @Value("${paymentservice.current-user.user-id}")
    private Long ISER_ID;
    @Value("${paymentservice.current-user.balance}")
    private Long BALANCE;

    public Mono<Boolean> checkUser (Long userId) {
        return Mono.just(Objects.equals(userId, ISER_ID));
    }

    public Mono <PaymentBalance> getBalance (Long userId) {
        PaymentBalance balance = new PaymentBalance();
        balance.setUserId(ISER_ID);
        balance.setBalance(BALANCE);
        return Mono.just(balance);
    }

    public Mono<PaymentStatus> getStatus(PaymentOrder paymentOrder) {
        PaymentStatus paymentStatus = new PaymentStatus();
        paymentStatus.setOrderId(paymentOrder.getOrderId());
        if (paymentOrder.getTotal() <= BALANCE) { paymentStatus.setStatus("accepted"); }
        else { paymentStatus.setStatus("rejected"); }
        return Mono.just(paymentStatus);
    }
}

