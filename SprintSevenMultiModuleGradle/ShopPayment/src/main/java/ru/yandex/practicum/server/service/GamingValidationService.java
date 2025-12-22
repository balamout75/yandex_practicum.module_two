package ru.yandex.practicum.server.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.model.PaymentBalance;
import ru.yandex.practicum.server.model.PaymentOrder;
import ru.yandex.practicum.server.model.PaymentStatus;

import static reactor.netty.http.HttpConnectionLiveness.log;


@Service
@ConditionalOnProperty(name = "paymentservice.game-mode", havingValue = "true")
public class GamingValidationService {

    public GamingValidationService() {    }
    @Value("${paymentservice.current-user.user-id}")
    private Long ISER_ID;
    @Value("${paymentservice.current-user.balance}")
    private Long BALANCE;

    public Mono<Boolean> checkUser(Long userId) {
        return Mono.just(playGame());
    }

    public Mono <PaymentBalance> getBalance (Long userId) {
        PaymentBalance balance = new PaymentBalance();
        balance.setUserId(ISER_ID);
        balance.setBalance(BALANCE);
        return Mono.just(balance);
    }

    public boolean playGame() {
        return Math.random() < 0.5;
    }

    public int playTernaryGame() {
        return (int) (Math.random() * 3) ;
    }

    public Mono<PaymentStatus> getStatus(PaymentOrder paymentOrder) {
        PaymentStatus paymentStatus = new PaymentStatus();
        paymentStatus.setOrderId(paymentOrder.getOrderId());
        String status = switch (this.playTernaryGame()) {
            case 0 -> "accepted";
            case 1 -> "rejected";
            default -> "unavailable";
        };
        log.info(status);
        paymentStatus.setStatus(status);
        return Mono.just(paymentStatus);
    }
}

