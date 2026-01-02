package ru.yandex.practicum.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import ru.yandex.practicum.configuration.BaseIntegrationTest;
import ru.yandex.practicum.controller.CartController;
import ru.yandex.practicum.service.payment.PaymentService;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOidcLogin;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SimpleSecuritySliceTests {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void accessAnonymousEndpoint() {
        webTestClient.get()
                .uri("/whoami")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void accessProtectedEndpointAnonymous() {
        webTestClient.get()
                .uri("/cart")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void loginWithMockOidcUser() {
        webTestClient.mutateWith(mockOidcLogin().idToken(token -> token.claim("roles", "USER")))
                .get()
                .uri("/cart")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void adminAccess() {
        webTestClient.mutateWith(mockOidcLogin().idToken(token -> token.claim("roles", "ADMIN")))
                .get()
                .uri("/items")
                .exchange()
                .expectStatus().isOk();
    }
}