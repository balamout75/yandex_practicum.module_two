package ru.yandex.practicum.service.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import ru.yandex.practicum.dto.payment.BalanceDto;
import ru.yandex.practicum.dto.payment.BalanceStatus;
import ru.yandex.practicum.dto.payment.ResultStatus;
import ru.yandex.practicum.dto.payment.StatusDto;
import ru.yandex.practicum.model.payment.PaymentBalance;
import ru.yandex.practicum.model.payment.PaymentOrder;
import ru.yandex.practicum.model.payment.PaymentStatus;

import java.time.Duration;
import java.util.Locale;


@Service
public class PaymentService {

    private final PaymentApi paymentApi;

    @Value("${payment.client.retry.attempts}")
    private int retryAttempts;

    @Value("${payment.client.retry.backoff}")
    private Duration retryBackoff;

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
                .retryWhen(Retry.backoff(retryAttempts, retryBackoff)
                        .filter(ex ->
                                !(ex instanceof WebClientResponseException.NotFound)
                        ))
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
                .map(response -> {
                    PaymentStatus paymentStatus = response.getBody();

                    ResultStatus resultStatus = switch (paymentStatus.getStatus()) {
                        case "accepted" -> ResultStatus.ACCEPTED;
                        case "rejected" -> ResultStatus.REJECTED;
                        default -> ResultStatus.UNAVAILABLE;
                    };

                    return new StatusDto(paymentStatus.getOrderId(), resultStatus);
                })
                .retryWhen(Retry.backoff(retryAttempts, retryBackoff)
                        .filter(ex ->
                                !(ex instanceof WebClientResponseException.NotFound)
                        ))
                .onErrorResume(WebClientResponseException.NotFound.class, ex ->
                        Mono.just(new StatusDto(0L, ResultStatus.NOT_FOUND))
                )
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.just(new StatusDto(0L, ResultStatus.UNAVAILABLE))
                );
    }
}