package ru.yandex.practicum.server.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.model.PaymentBalance;
import ru.yandex.practicum.server.model.PaymentOrder;
import ru.yandex.practicum.server.model.PaymentStatus;
import ru.yandex.practicum.server.service.GamingValidationService;
import ru.yandex.practicum.server.service.PaymentValidationService;

import static org.mockito.Mockito.when;

//@WebFluxTest(controllers = PaymentController.class)
@SpringBootTest
@AutoConfigureWebTestClient
class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PaymentValidationService paymentService;


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
        balance.setBalance(50000L);

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
                .jsonPath("$.balance").isEqualTo(50000);
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

}
