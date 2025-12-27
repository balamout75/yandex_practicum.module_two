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
import ru.yandex.practicum.model.shoping.CartItem;
import ru.yandex.practicum.security.CurrentUserFacade;
import ru.yandex.practicum.service.shoping.CartItemService;

import java.time.Duration;
import java.util.Locale;

import static reactor.netty.http.HttpConnectionLiveness.log;


@Service
public class PaymentService {

    private final PaymentApi paymentApi;
    private final CurrentUserFacade currentUserFacade;
    private final CartItemService cartItemService;

    @Value("${payment.client.retry.attempts}")
    private int retryAttempts;

    @Value("${payment.client.retry.backoff}")
    private Duration retryBackoff;

    public PaymentService(PaymentApi paymentApi,
                          CurrentUserFacade currentUserFacade,
                          CartItemService cartItemService) {
        this.paymentApi = paymentApi;
        this.currentUserFacade = currentUserFacade;
        this.cartItemService = cartItemService;
    }

    public Mono<BalanceDto> getBalance() {
        return this.currentUserFacade.getUserId()
                .flatMap(this::getBalanceForUser);
    }

    Mono<BalanceDto> getBalanceForUser(Long userId) {
        log.info("Getting balance for user {}", userId);
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

    public Mono<StatusDto> buy() {
        return this.currentUserFacade.getUserId()
                .zipWhen(userId -> cartItemService.getCartTotal())
                .flatMap(pair->this.buyForUser(pair.getT1(), pair.getT2()));
    }

    Mono<StatusDto> buyForUser(Long userId, Long total) {
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
                    return new StatusDto(paymentStatus.getOrderId(), total, resultStatus);
                })
                .retryWhen(Retry.backoff(retryAttempts, retryBackoff)
                        .filter(ex ->
                                !(ex instanceof WebClientResponseException.NotFound)
                        ))
                .onErrorResume(WebClientResponseException.NotFound.class, ex ->
                        Mono.just(new StatusDto(0L, total, ResultStatus.NOT_FOUND))
                )
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.just(new StatusDto(0L, total, ResultStatus.UNAVAILABLE))
                );
    }
}