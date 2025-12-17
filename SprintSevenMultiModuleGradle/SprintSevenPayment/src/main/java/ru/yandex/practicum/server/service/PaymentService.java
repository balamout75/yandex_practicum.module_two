package ru.yandex.practicum.server.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.model.PaymentBalance;
import ru.yandex.practicum.server.model.PaymentOrder;
import ru.yandex.practicum.server.model.PaymentStatus;



@Service
public class PaymentService {

    public PaymentService() {    }
    @Value("${paymentservice.currentuser.userid}")
    private Long ISERID;
    @Value("${paymentservice.currentuser.balance}")
    private Long BALANCE;
    @Value("${paymentservice.avaible}")
    private boolean AVAILABLE;


    public Mono<Boolean> checkUser (Long userId) {
        return Mono.just(ISERID.equals(userId));
    }

    public Mono <PaymentBalance> getBalance (Long userId) {
        if (!AVAILABLE) return Mono.empty();
        PaymentBalance balance = new PaymentBalance(); balance.setUserId(ISERID); balance.setBalance(BALANCE);
        return Mono.just(balance);
    }

    public Mono<PaymentStatus> getStatus(PaymentOrder paymentOrder) {
        if (!AVAILABLE) {
            return Mono.empty();
        }

        PaymentStatus paymentStatus = new PaymentStatus();
        paymentStatus.setOrderId(paymentOrder.getOrderId());

        if (paymentOrder.getTotal() <= BALANCE) {
            paymentStatus.setStatus("success");
        } else {
            paymentStatus.setStatus("rejected");
        }

        return Mono.just(paymentStatus);
    }
}

