package ru.yandex.practicum.service;


import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.r2dbc.core.DatabaseClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.yandex.practicum.configuration.TestcontainersCustomConfiguration;
import ru.yandex.practicum.mapper.ActionModes;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static reactor.netty.http.HttpConnectionLiveness.log;

@SpringBootTest
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
    private ConnectionFactory connectionFactory;



    @Test
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
        assertEquals(2, orderService.findOrder(USER_ID, 2L).block().items().size());
        //SQL работать отказалась, придумал костыль
        baseCleaner1();
    }

    private void baseCleaner1() {
        DatabaseClient client = DatabaseClient.create(connectionFactory);
        Mono<Long> deleteRows = client.sql("delete from orders where id = :order").bind("order", 2).fetch().rowsUpdated();
        Mono<Long> insertRows = client.sql("insert into cart_items values (1, 4, 1), (1, 5, 3)").fetch().rowsUpdated();
        Mono<Long> reestoredRows = deleteRows.zipWhen(x -> insertRows.map(y -> y + x)).map(Tuple2::getT2);
        log.info("Affected rows 1: " + reestoredRows.block());
    }


    public record ChangeInCardCountRequest(long userId, long itemId, ActionModes actionModes) {
    }

    static Stream<Arguments> applyInCardCountRequest() {
        return Stream.of(Arguments.of(new ChangeInCardCountRequest(1, 1, ActionModes.MINUS)),     //отсутствует
                Arguments.of(new ChangeInCardCountRequest(1, 1, ActionModes.PLUS)),      //добавили первый элемент
                Arguments.of(new ChangeInCardCountRequest(1, 1, ActionModes.DELETE)),    //удалили пустую коллекцию
                Arguments.of(new ChangeInCardCountRequest(1, 1, ActionModes.NOTHING)),   //NOTHING
                Arguments.of(new ChangeInCardCountRequest(1, 4, ActionModes.MINUS)),     //удалили один элемент из одного
                Arguments.of(new ChangeInCardCountRequest(1, 4, ActionModes.PLUS)),      //добавили второй элемент
                Arguments.of(new ChangeInCardCountRequest(1, 4, ActionModes.DELETE)),    //удалили не пустую коллекцию
                Arguments.of(new ChangeInCardCountRequest(1, 4, ActionModes.NOTHING)),   //NOTHING
                Arguments.of(new ChangeInCardCountRequest(1, 5, ActionModes.MINUS))     //удалили один элемент из трех
        );
    }

    //тест действий нв изменение количества элементов в корзине
    //поскольку тест меняет содержимое, обновляю базу после каждого теста
    @ParameterizedTest
    @MethodSource("applyInCardCountRequest")
    void testChangeInCardCount(ChangeInCardCountRequest request) {
        long userId = request.userId();
        long itemId = request.itemId();
        ActionModes actionMode = request.actionModes();
        long initialCount = chartService.findItem(userId, itemId).map(x -> x.count()).block();

        cartItemService.changeInCardCount(userId, itemId, actionMode).block();

        long resultCount  = chartService.findItem(userId, itemId).map(x -> x.count()).block();

        long trueCount = switch (actionMode) {
            case ActionModes.PLUS -> initialCount + 1;
            case ActionModes.MINUS -> initialCount > 1 ? initialCount - 1 : 0;
            case ActionModes.DELETE -> 0L;
            case NOTHING -> initialCount;
        };

        assertEquals(trueCount, resultCount);
        baseCleaner2();
    }
    private void baseCleaner2() {
        DatabaseClient client = DatabaseClient.create(connectionFactory);
        Mono<Long> deleteRows = client.sql("delete from cart_items").fetch().rowsUpdated();
        Mono<Long> insertRows = client.sql("insert into cart_items values (1, 4, 1), (1, 5, 3)").fetch().rowsUpdated();
        Mono<Long> reestoredRows = deleteRows.zipWhen(x -> insertRows.map(y -> y + x)).map(Tuple2::getT2);

        log.info("Affected rows 2: " + reestoredRows.block());
    }

}
