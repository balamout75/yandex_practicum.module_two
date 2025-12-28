package ru.yandex.practicum.service.shoping;


import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.yandex.practicum.configuration.BaseIntegrationTest;
import ru.yandex.practicum.configuration.TestOAuth2Config;
import ru.yandex.practicum.dto.shoping.ItemDto;
import ru.yandex.practicum.mapper.ActionModes;
import ru.yandex.practicum.security.CurrentUserFacade;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static reactor.netty.http.HttpConnectionLiveness.log;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestOAuth2Config.class)
class UserServiceLimitedIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private CatalogueService catalogueService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private ReactiveRedisTemplate<String, ItemDto> itemRedisTemplate;

    @MockitoBean
    CurrentUserFacade currentUserFacade;

    @Test
    void testCloseChartByUserId() {
        //Изначально в корзине два товара
        doReturn(Mono.just(1L)).when(currentUserFacade).getUserId();
        assertEquals(2, cartItemService.findForUser(currentUserFacade.getUserId().block()).collectList().block().size());
        //запоминаем количество заказов, в тестовой схеме их 1
        assertEquals(1, orderService.findOrders().collectList().block().size());
        //Создаем новый заказ на основе существующей корзины
        orderService.closeCart().block();
        //убеждаемся, что корзина пуста
        assertEquals(0, cartItemService.getInCartItems().collectList().block().size());
        //количество заказов + 1
        assertEquals(2, orderService.findOrders().collectList().block().size());
        //в новом заказе две позиции. При необходимости можно уточнить, какие, и те ли, что были в корзине
        assertEquals(2, orderService.findOrder(2L).block().items().size());
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
                Arguments.of(new ChangeInCardCountRequest(1, 5, ActionModes.MINUS))      //удалили один элемент из трех
        );
    }

    //тест действий нв изменение количества элементов в корзине
    //поскольку тест меняет содержимое, обновляю базу после каждого теста
    @ParameterizedTest
    @MethodSource("applyInCardCountRequest")
    void testChangeInCardCount(ChangeInCardCountRequest request) {
        doReturn(Mono.just(1L)).when(currentUserFacade).getUserId();
        long itemId = request.itemId();
        ActionModes actionMode = request.actionModes();
        long initialCount = catalogueService.findItem(itemId).map(x -> x.count()).block();
        cartItemService.changeInCardCount(itemId, actionMode).block();
        long resultCount  = catalogueService.findItem(itemId).map(x -> x.count()).block();
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
        itemRedisTemplate.opsForHash().remove("item:1", "1").block();
        itemRedisTemplate.opsForHash().remove("item:1", "4").block();
        itemRedisTemplate.opsForHash().remove("item:1", "5").block();
        Mono<Long> reestoredRows = deleteRows.zipWhen(x -> insertRows.map(y -> y + x)).map(Tuple2::getT2);
        log.info("Affected rows 2: " + reestoredRows.block());
    }
}
