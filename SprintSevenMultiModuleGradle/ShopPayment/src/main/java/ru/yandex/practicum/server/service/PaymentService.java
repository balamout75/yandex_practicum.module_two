package ru.yandex.practicum.server.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.model.PaymentBalance;
import ru.yandex.practicum.server.model.PaymentOrder;
import ru.yandex.practicum.server.model.PaymentStatus;

import java.util.Objects;

import static reactor.netty.http.HttpConnectionLiveness.log;


@Service
public class PaymentService {

    public PaymentService() {    }
    @Value("${paymentservice.current-user.user-id}")
    private Long ISER_ID;
    @Value("${paymentservice.current-user.balance}")
    private Long BALANCE;
    @Value("${paymentservice.game-mode}")
    private boolean GAME_MODE;


    public Mono<Boolean> checkUser (Long userId) {
        if (GAME_MODE) { return checkGamingUser(userId); }
        else { return  checkRealUser(userId); }
    }

    private Mono<Boolean> checkGamingUser(Long userId) {
        return Mono.just(playGame());
    }

    public Mono<Boolean> checkRealUser (Long userId) {
        return Mono.just(Objects.equals(userId, ISER_ID));
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
        if (GAME_MODE) { return getGamingStatus (paymentOrder); }
        else { return  getRealStatus(paymentOrder); }
    }

    private Mono<PaymentStatus> getGamingStatus(PaymentOrder paymentOrder) {
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

    private Mono<PaymentStatus> getRealStatus(PaymentOrder paymentOrder) {
        PaymentStatus paymentStatus = new PaymentStatus();
        paymentStatus.setOrderId(paymentOrder.getOrderId());
        if (paymentOrder.getTotal() <= BALANCE) {
            paymentStatus.setStatus("accepted");
        } else {
            paymentStatus.setStatus("rejected");
        }
        return Mono.just(paymentStatus);
    }
}

