package ru.yandex.practicum.server.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import ru.yandex.practicum.server.api.PaymentApi;

@TestConfiguration
public class TestPaymentClientConfiguration {

    @Bean
    WebClient paymentWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081") // 👈 рабочий сервер
                .build();
    }

    @Bean
    PaymentApi paymentApi(WebClient paymentWebClient) {
        return HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(paymentWebClient))
                .build()
                .createClient(PaymentApi.class);
    }
}
