package ru.yandex.practicum.service.payment;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.payment.BalanceDto;
import ru.yandex.practicum.dto.payment.BalanceStatus;
import ru.yandex.practicum.dto.payment.ResultStatus;
import ru.yandex.practicum.dto.payment.StatusDto;
import ru.yandex.practicum.model.payment.PaymentBalance;
import ru.yandex.practicum.model.payment.PaymentOrder;
import ru.yandex.practicum.model.payment.PaymentStatus;

import java.util.Locale;


@Service
public class PaymentService {

    private final PaymentApi paymentApi;

    public PaymentService(PaymentApi paymentApi) {
        this.paymentApi = paymentApi;
    }

    public Mono<BalanceDto> getBalance(Long userId) {
        return paymentApi.paymentUserIdBalanceGet(userId)
                .map(response -> {
                    PaymentBalance paymentBalance = response.getBody();
                    return new BalanceDto(
                            paymentBalance.getUserId(),
                            paymentBalance.getBalance(),
                            BalanceStatus.ACCEPTED
                    );
                })
                .onErrorResume(WebClientResponseException.NotFound.class, ex ->{
                        String body = ex.getResponseBodyAsString();
                        BalanceStatus status = body.contains("Пользователь не зарегистрирован")
                                ? BalanceStatus.USER_NOT_FOUND
                                : BalanceStatus.SERVICE_NOT_FOUND;
                        return Mono.just(new BalanceDto(userId,0L,status));}
                )
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.just(new BalanceDto(userId,0L, BalanceStatus.SERVICE_UNAVAILABLE))
                );
    }

    public Mono<StatusDto> buy(Long userId, Long total) {
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setUserId(userId);
        paymentOrder.setOrderId(1L);
        paymentOrder.setTotal(total);
        return paymentApi.paymentUserIdBuyPost(userId, paymentOrder)
                .flatMap(response -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        PaymentStatus  paymentStatus = response.getBody();
                        ResultStatus resultStatus = ResultStatus.REFUSED;
                        System.out.println("Статус платежа "+paymentStatus.getStatus());
                        if (paymentStatus.getStatus().equalsIgnoreCase("accepted")) { resultStatus = ResultStatus.ACCEPTED; }
                        return Mono.just(new StatusDto(paymentStatus.getOrderId(),resultStatus));
                    } else return Mono.just(new StatusDto(0L, ResultStatus.UNAVAILABLE));
                })
                .onErrorResume(WebClientResponseException.NotFound.class, ex ->{
                    String body = ex.getResponseBodyAsString();
                    ResultStatus status = ResultStatus.NOT_FOUND;
                    return Mono.just(new StatusDto(0L,status));}
                )
                .onErrorResume(WebClientResponseException.class, ex ->
                    Mono.just(new StatusDto(0L,ResultStatus.UNAVAILABLE))
                );
    }
}