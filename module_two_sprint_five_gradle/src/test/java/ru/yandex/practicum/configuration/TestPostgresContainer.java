package ru.yandex.practicum.configuration;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public class TestPostgresContainer extends PostgreSQLContainer<TestPostgresContainer> {
    private static final String IMAGE_VERSION = "postgres:18.1";
    @Container
    @ServiceConnection
    private static TestPostgresContainer container;

    private TestPostgresContainer() {
        super(IMAGE_VERSION);
        this.withInitScript("schema.sql");
    }

    public static TestPostgresContainer getInstance() {
        if (container == null) {
            container = new TestPostgresContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("TEST_URL", container.getJdbcUrl());
        System.setProperty("TEST_USERNAME", container.getUsername());
        System.setProperty("TEST_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
    }
}