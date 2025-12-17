package ru.yandex.practicum.server.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.model.Balance;
import ru.yandex.practicum.server.model.Order;
import ru.yandex.practicum.server.model.Status;


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

    public Mono <Balance> getBalance (Long userId) {
        if (!AVAILABLE) return Mono.empty();
        Balance balance = new Balance(); balance.setUserId(ISERID); balance.setBalance(BALANCE);
        return Mono.just(balance);
    }

    public Mono<Status> getStatus(Order order) {
        if (!AVAILABLE) {
            return Mono.empty();
        }

        Status status = new Status();
        status.setOrderId(order.getOrderId());

        if (order.getTotal() <= BALANCE) {
            status.setStatus("success");
        } else {
            status.setStatus("rejected");
        }

        return Mono.just(status);
    }
}

