package ru.yandex.practicum.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import ru.yandex.practicum.service.payment.PaymentApi;


@Configuration
public class PaymentClientConfiguration {
    @Value("${payment_service_url}")
    private String paymentServiceUrl;

    @Bean
    WebClient paymentWebClient() {
        return WebClient.builder()
                .baseUrl(paymentServiceUrl)
                .build();
    }

    @Bean
    PaymentApi paymentApi(WebClient paymentWebClient) {
        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory
                        .builderFor(WebClientAdapter.create(paymentWebClient))
                        .build();

        return factory.createClient(PaymentApi.class);
    }
}