package ru.yandex.practicum.configuration;

import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.postgresql.PostgreSQLR2DBCDatabaseContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.utility.DockerImageName;

import static java.lang.String.format;

public class TestcontainersCustomConfiguration {

    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:18.1");

    @Container
    @ServiceConnection
    protected static final PostgreSQLR2DBCDatabaseContainer postgresContainer;
    protected static final ConnectionFactoryOptions options;

    static {
        PostgreSQLContainer container = new PostgreSQLContainer(POSTGRES_IMAGE)
                .withDatabaseName   ("testdb")
                .withUsername       ("testuser")
                .withPassword       ("testpass")
                .withInitScript     ("schema.sql");

        container.start();
        postgresContainer = new PostgreSQLR2DBCDatabaseContainer(container);
        options = PostgreSQLR2DBCDatabaseContainer.getOptions(container);
    }

    @DynamicPropertySource
    static void setR2dbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> format("r2dbc:pool:postgresql://%s:%s/%s",
                options.getValue(ConnectionFactoryOptions.HOST).toString(),
                options.getValue(ConnectionFactoryOptions.PORT).toString(),
                options.getValue(ConnectionFactoryOptions.DATABASE).toString()));
        registry.add("spring.r2dbc.username", () -> options.getValue(ConnectionFactoryOptions.USER).toString());
        registry.add("spring.r2dbc.password", () -> options.getValue(ConnectionFactoryOptions.PASSWORD).toString());
        registry.add("spring.sql.init.mode" , () -> "never");
    }
}
