package ru.yandex.practicum.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import ru.yandex.practicum.configuration.TestPaymentClientConfiguration;
import ru.yandex.practicum.service.PaymentService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        PaymentService.class,
        TestPaymentClientConfiguration.class
})
class PaymentServiceTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    MockWebServer mockWebServer;

    @Test
    void getBalance_success() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody("""
                                {
                                  "userId": 1,
                                  "balance": 100
                                }
                                """)
        );

        StepVerifier.create(paymentService.getBalance(1L))
                .expectNextMatches(b ->
                        b.getUserId() == 1L && b.getBalance() == 100
                )
                .verifyComplete();
    }
}