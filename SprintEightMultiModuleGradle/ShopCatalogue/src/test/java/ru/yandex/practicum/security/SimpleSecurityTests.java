package ru.yandex.practicum.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.configuration.BaseIntegrationTest;
import ru.yandex.practicum.configuration.TestOAuth2Config;
import ru.yandex.practicum.mapper.ActionModes;
import ru.yandex.practicum.mapper.SortModes;
import ru.yandex.practicum.service.shoping.CartItemService;
import ru.yandex.practicum.service.shoping.CatalogueService;

import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOidcLogin;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SimpleSecurityTests extends BaseIntegrationTest {

    @Value("${images.path}")
    private String UPLOAD_DIR;

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    @MockitoBean
    private WebClient paymentWebClient;


    @Test
    void simpleSecurityTest() {
        webTestClient.get()
                .uri("/items")
                .exchange()
                .expectStatus().isOk();
    }

    /*
    @Test
    void testAccepterPostRequest() {
        webTestClient.mutateWith(mockOidcLogin()
                .idToken(token -> token
                        .subject("user-123")
                        .claim("preferred_username", "SomeUser")
                        )
                )
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam("id",1)
                        .queryParam("search", "")
                        .queryParam("sort", SortModes.ALPHA.toString())
                        .queryParam("pageNumber", "1")
                        .queryParam("pageSize", "5")
                        .queryParam("action",  ActionModes.NOTHING.toString())
                        .build())
                .exchange()
                .expectStatus().isOk();
     }

    @Test
    void testRejectedPostRequest() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam("id",1)
                        .queryParam("search", "")
                        .queryParam("sort", SortModes.ALPHA.toString())
                        .queryParam("pageNumber", "1")
                        .queryParam("pageSize", "5")
                        .queryParam("action",  ActionModes.NOTHING.toString())
                        .build())
                .exchange()
                .expectStatus().is4xxClientError();
    }

     */
}
