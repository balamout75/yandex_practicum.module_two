package ru.yandex.practicum.server.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.model.PaymentBalance;
import ru.yandex.practicum.server.service.PaymentValidationService;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ControllerAccessTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private PaymentValidationService paymentService;

    @Test
    @WithMockUser(authorities = "SERVICE")
    void getBalance_success_returns200() {
        PaymentBalance balance = new PaymentBalance();
        balance.setUserId(1L);
        balance.setBalance(50000L);
        when(paymentService.checkUser(1L)).thenReturn(Mono.just(true));
        when(paymentService.getBalance(1L)).thenReturn(Mono.just(balance));
        webClient.get()
                .uri("/payment/1/balance")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.userId").isEqualTo(1)
                .jsonPath("$.balance").isEqualTo(50000);
    }

    @Test
    void getBalance_rejected_returns401() {
        webClient.get()
                .uri("/payment/1/balance")
                .exchange()
                .expectStatus().isUnauthorized();;
    }
}
