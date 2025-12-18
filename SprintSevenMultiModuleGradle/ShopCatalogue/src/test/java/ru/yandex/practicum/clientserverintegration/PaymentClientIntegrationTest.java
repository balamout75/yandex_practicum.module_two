package ru.yandex.practicum.clientserverintegration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;
import ru.yandex.practicum.configuration.TestPaymentClientConfiguration;
import ru.yandex.practicum.service.payment.PaymentApi;
import ru.yandex.practicum.model.payment.PaymentBalance;
import ru.yandex.practicum.model.payment.PaymentOrder;
import ru.yandex.practicum.model.payment.PaymentStatus;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestPaymentClientConfiguration.class)
class PaymentClientIntegrationTest {

    @Autowired
    PaymentApi paymentApi;

    // ---------- GET /balance SUCCESS ----------
    @Test
    void getBalance_success() {
        StepVerifier.create(
                        paymentApi.paymentUserIdBalanceGet(1L)
                )
                .assertNext(response -> {
                    assert response.getStatusCode().is2xxSuccessful();

                    PaymentBalance body = response.getBody();
                    assert body != null;
                    assert body.getUserId().equals(1L);
                    assert body.getBalance().equals(50000L);
                })
                .verifyComplete();
    }

    // ---------- GET /balance 404 ----------
    @Test
    void getBalance_userNotFound() {
        StepVerifier.create(
                        paymentApi.paymentUserIdBalanceGet(9999L)
                )
                .expectErrorMatches(ex ->
                        ex instanceof WebClientResponseException.NotFound
                )
                .verify();
    }

    // ---------- POST /buy SUCCESS ----------
    @Test
    void buy_success() {
        PaymentOrder order = new PaymentOrder();
        order.setUserId(1L);
        order.setOrderId(1L);
        order.setTotal(10L);

        StepVerifier.create(
                        paymentApi.paymentUserIdBuyPost(1L, order)
                )
                .assertNext(response -> {
                    assert response.getStatusCode().is2xxSuccessful();

                    PaymentStatus status = response.getBody();
                    assert status != null;
                    assert status.getOrderId().equals(1L);
                    assert status.getStatus().equals("accepted");
                })
                .verifyComplete();
    }

    // ---------- POST /buy 404 ----------
    @Test
    void buy_userNotFound() {
        PaymentOrder order = new PaymentOrder();
        order.setUserId(9999L);
        order.setOrderId(1L);
        order.setTotal(10L);

        StepVerifier.create(
                        paymentApi.paymentUserIdBuyPost(9999L, order)
                )
                .expectErrorMatches(ex ->
                        ex instanceof WebClientResponseException.NotFound
                )
                .verify();
    }

    // ---------- POST /buy 500 ----------
    @Test
    void buy_serviceUnavailable() {
        PaymentOrder order = new PaymentOrder();
        order.setUserId(1L);
        order.setOrderId(1L);
        order.setTotal(9999999L);

        StepVerifier.create(
                        paymentApi.paymentUserIdBuyPost(1L, order)
                )
                .assertNext(response -> {
                    assert response.getStatusCode().is2xxSuccessful();

                    PaymentStatus status = response.getBody();
                    assert status != null;
                    assert status.getOrderId().equals(1L);
                    assert status.getStatus().equals("refused");
                })
                .verifyComplete();
    }
}
