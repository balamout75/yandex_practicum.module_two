package ru.yandex.practicum.service;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.yandex.practicum.configuration.TestPostgresContainer;
import ru.yandex.practicum.mapping.ActionModes;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest
@ImportTestcontainers(TestPostgresContainer.class)
class UserServiceLimitedIntegrationTest {

    @ClassRule
    public static PostgreSQLContainer<TestPostgresContainer> postgreSQLContainer = TestPostgresContainer.getInstance();

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;


    //тест на перенос элементов из корзины в заказ
    @Test
    void testCloseChartByUserId() {
        User user = userService.getUser(1L);
        //проверяем наличие товаров в корзине, в тестовой схеме их 2
        assertEquals(2, user.getInCarts().size());
        //запоминаем количество заказов, в тестовой схеме их 1
        assertEquals(1, user.getOrders().size());
        //Создаем новый заказ на основе существующей корзины
        orderService.closeCart(user.getId());
        //актуализируем сущность User
        user = userService.getUser(user.getId());
        //убеждаемся, что корзина пуста
        assertEquals(0, user.getInCarts().size());
        //количество заказов + 1
        assertEquals(2, user.getOrders().size());
        //в новом заказе две позиции. При необходимости можно уточнить, какие, и те ли, что были в корзине
        int orderSize = user.getOrders().stream().filter(u -> u.getId()==2).map(u->u.getInOrder().size()).findFirst().orElse(0);
        assertEquals(2, orderSize);
    }

    public record ChangeInCardCountRequest(
            long userId,
            long itemId,
            ActionModes actionModes
    ) {}

    static Stream<Arguments> applyInCardCountRequest() {
        return Stream.of(
                Arguments.of(new ChangeInCardCountRequest(1,1, ActionModes.MINUS)),     //отсутствует
                Arguments.of(new ChangeInCardCountRequest(1,1, ActionModes.PLUS)),      //добавили первый элемент
                Arguments.of(new ChangeInCardCountRequest(1,1, ActionModes.DELETE)),    //удалили пустую коллекцию
                Arguments.of(new ChangeInCardCountRequest(1,1, ActionModes.NOTHING)),   //NOTHING
                Arguments.of(new ChangeInCardCountRequest(1,4, ActionModes.MINUS)),     //удалили один элемент из одного
                Arguments.of(new ChangeInCardCountRequest(1,4, ActionModes.PLUS)),      //добавили второй элемент
                Arguments.of(new ChangeInCardCountRequest(1,4, ActionModes.DELETE)),    //удалили не пустую коллекцию
                Arguments.of(new ChangeInCardCountRequest(1,4, ActionModes.NOTHING)),   //NOTHING
                Arguments.of(new ChangeInCardCountRequest(1,5, ActionModes.MINUS))     //удалили один элемент из трех
        );
    }
    //тест действий нв изменение количества элементов в корзине
    //поскольку тест меняет содержимое, обновляю базу после каждого теста
    @ParameterizedTest
    @MethodSource("applyInCardCountRequest")
    @Sql(scripts = "/schema.sql", executionPhase = AFTER_TEST_METHOD)
    void testChangeInCardCount(ChangeInCardCountRequest request) {
        long userId = request.userId();
        long itemId = request.itemId();
        ActionModes actionMode = request.actionModes();
        long initialCount = userService.getUser(userId).getInCarts().stream()
                                .filter(u-> u.getItem().getId().equals(itemId))
                                .map(CartItem::getCount)
                                .findFirst()
                                .orElse(0L);

        cartService.changeInCardCount(userId, itemId, actionMode);

        long resultCount = userService.getUser(userId).getInCarts().stream()
                .filter(u-> u.getItem().getId()==itemId)
                .map(CartItem::getCount)
                .findFirst()
                .orElse(0L);

        long trueCount = switch (actionMode) {
            case ActionModes.PLUS   -> initialCount+1 ;
            case ActionModes.MINUS  -> initialCount>1 ? initialCount-1 : 0 ;
            case ActionModes.DELETE -> 0L ;
            case NOTHING            -> initialCount;
        };

        assertEquals(trueCount, resultCount);
    }
 }
