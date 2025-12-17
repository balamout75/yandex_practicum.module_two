package ru.yandex.practicum.configuration;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static java.lang.String.format;

@Testcontainers
@Import(EmbeddedRedisConfiguration.class)
public abstract class BaseIntegrationTest {

    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:18.1");

    @Container
    //@ServiceConnection
    protected static final PostgreSQLContainer postgres = new PostgreSQLContainer(POSTGRES_IMAGE)
                .withDatabaseName   ("testdb")
                .withUsername       ("testuser")
                .withPassword       ("testpass")
                .withInitScript     ("schema.sql");

    @DynamicPropertySource
    static void r2dbcAndRedisProperties(DynamicPropertyRegistry registry) {
        // ---------- REDIS ----------

        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> EmbeddedRedisConfiguration.REDIS_PORT);

        // ---------- POSTGRES ----------
        registry.add("spring.r2dbc.url", () ->
                String.format(
                        "r2dbc:postgresql://%s:%d/%s",
                        postgres.getHost(),
                        postgres.getMappedPort(5432),
                        postgres.getDatabaseName()
                )
        );
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
        registry.add("spring.sql.init.mode", () -> "never");
    }
}
