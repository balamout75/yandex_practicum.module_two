package ru.yandex.practicum.service;


import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.configuration.TestcontainersCustomConfiguration;

import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.mapper.ActionModes;
import ru.yandex.practicum.mapper.SortModes;
import ru.yandex.practicum.service.CartItemService;
import ru.yandex.practicum.service.ChartService;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
@SpringBootTest
@AutoConfigureWebTestClient
@Testcontainers
@ImportTestcontainers(TestcontainersCustomConfiguration.class)
class UserServiceLimitedIntegrationTests {

    private static final long USER_ID = 1;

    @Autowired
    private ChartService chartService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DataSource dataSource;

    @Test
    @Sql(scripts = "/patch.sql")
    void testCloseChartByUserId() {
        //Изначально в корзине два товара
        assertEquals(2, cartItemService.findByUserId(USER_ID).collectList().block().size());
        //запоминаем количество заказов, в тестовой схеме их 1
        assertEquals(1, orderService.findOrders(USER_ID).collectList().block().size());
        //Создаем новый заказ на основе существующей корзины
        orderService.closeCart(USER_ID).block();
        //убеждаемся, что корзина пуста
        assertEquals(0, cartItemService.getCartCount(USER_ID).blockOptional().orElse(0L));
        //количество заказов + 1
        assertEquals(2, orderService.findOrders(USER_ID).collectList().block().size());
        //в новом заказе две позиции. При необходимости можно уточнить, какие, и те ли, что были в корзине
        assertEquals(2, orderService.findOrder(USER_ID,2L).block().items().size());
        //session.close();
    }


}
