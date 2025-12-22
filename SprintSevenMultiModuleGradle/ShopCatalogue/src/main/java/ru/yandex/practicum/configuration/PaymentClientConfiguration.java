package ru.yandex.practicum.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;
import ru.yandex.practicum.service.payment.PaymentApi;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class PaymentClientConfiguration {

    @Value("${payment.client.base-url}")
    private String baseUrl;
    @Value("${payment.client.timeout.connect}")
    private Duration connectTimeout;
    @Value("${payment.client.timeout.response}")
    private Duration responseTimeout;
    @Value("${payment.client.retry.attempts}")
    private int retryAttempts;
    @Value("${payment.client.retry.backoff}")
    private Duration retryBackoff;

    @Bean
    WebClient paymentWebClient() {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectTimeout.toMillis())
                .responseTimeout(responseTimeout)
                .doOnConnected(conn ->
                        conn.addHandlerLast(
                                new ReadTimeoutHandler(
                                        responseTimeout.toSeconds(),
                                        TimeUnit.SECONDS
                                )
                        ).addHandlerLast(
                                new WriteTimeoutHandler(
                                        responseTimeout.toSeconds(),
                                        TimeUnit.SECONDS
                                )
                        )
                );

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
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
