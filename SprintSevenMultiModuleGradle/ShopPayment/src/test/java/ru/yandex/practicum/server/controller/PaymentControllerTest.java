package ru.yandex.practicum.server.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.model.PaymentBalance;
import ru.yandex.practicum.server.model.PaymentOrder;
import ru.yandex.practicum.server.model.PaymentStatus;
import ru.yandex.practicum.server.service.GamingValidationService;

import static org.mockito.Mockito.when;

//@WebFluxTest(controllers = PaymentController.class)
@SpringBootTest
@AutoConfigureWebTestClient
class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GamingValidationService paymentService;

    // ---------- BALANCE ----------

    @Test
    void getBalance_userNotRegistered_returns404WithMessage() {
        when(paymentService.checkUser(2L))
                .thenReturn(Mono.just(false));

        webTestClient.get()
                .uri("/payment/2/balance")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("Пользователь не зарегистрирован");
    }

    @Test
    void getBalance_success_returns200() {
        PaymentBalance balance = new PaymentBalance();
        balance.setUserId(1L);
        balance.setBalance(1000L);

        when(paymentService.checkUser(1L))
                .thenReturn(Mono.just(true));
        when(paymentService.getBalance(1L))
                .thenReturn(Mono.just(balance));

        webTestClient.get()
                .uri("/payment/1/balance")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.userId").isEqualTo(1)
                .jsonPath("$.balance").isEqualTo(1000);
    }

    @Test
    void getBalance_serviceUnavailable_returns500() {
        when(paymentService.checkUser(1L))
                .thenReturn(Mono.just(true));
        when(paymentService.getBalance(1L))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/payment/1/balance")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Сервис не доступен");
    }

    // ---------- BUY ----------

    @Test
    void buy_userNotRegistered_returns404() {
        when(paymentService.checkUser(2L))
                .thenReturn(Mono.just(false));

        webTestClient.post()
                .uri("/payment/2/buy")
                .bodyValue(new PaymentOrder())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("Пользователь не зарегистрирован");

    }

    @Test
    void buy_success_returns200() {
        PaymentOrder order = new PaymentOrder();
        order.setOrderId(10L);
        order.setTotal(500L);

        PaymentStatus status = new PaymentStatus();
        status.setOrderId(10L);
        status.setStatus("success");

        when(paymentService.checkUser(1L))
                .thenReturn(Mono.just(true));
        when(paymentService.getStatus(order))
                .thenReturn(Mono.just(status));

        webTestClient.post()
                .uri("/payment/1/buy")
                .bodyValue(order)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.orderId").isEqualTo(10)
                .jsonPath("$.status").isEqualTo("success");
    }
}
