package ru.yandex.practicum.repository;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.yandex.practicum.configuration.TestPostgresContainer;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.model.Order;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ImportTestcontainers(TestPostgresContainer.class)
class OrderRepositoryJpaTest {

    @ClassRule
    public static PostgreSQLContainer<TestPostgresContainer> postgreSQLContainer = TestPostgresContainer.getInstance();

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void findOrdersByUserId() {
        User user = new User();
        user.setId(1L);
        List<Order> all = orderRepository.findByUser(user);
        assertEquals(1,all.size());
    }
 }
