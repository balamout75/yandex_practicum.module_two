package ru.yandex.practicum.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.WebSessionServerCsrfTokenRepository;
import ru.yandex.practicum.security.UserEnrichmentFilter;
import ru.yandex.practicum.security.UserPrincipal;

import java.net.URI;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {
    // Защищаем пароли шифрованием
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSessionServerCsrfTokenRepository csrfTokenRepository() {
        return new WebSessionServerCsrfTokenRepository();
    }

    // Создаём in-memory пользователя
    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }

    // Настраиваем поведение при выходе
    @Bean
    public RedirectServerLogoutSuccessHandler redirectServerLogoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler logoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        // При выходе перенаправляем его на домашнюю страницу
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("/items"));
        return logoutSuccessHandler;
    }

    @Bean
    public ServerLogoutSuccessHandler keycloakLogoutHandler() {
        return (exchange, authentication) -> {
            String logoutUrl ="http://localhost:8080/realms/demo-realm/protocol/openid-connect/logout"
                            + "?redirect_uri=http://localhost:8081/items";
            exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
            exchange.getExchange().getResponse().getHeaders().setLocation(URI.create(logoutUrl));
            return exchange.getExchange().getResponse().setComplete();
        };
    }

    @Bean
    public ServerLogoutSuccessHandler keycloakLogoutHandler2() {
        return (exchange, authentication) -> {

            String idToken = null;

            if (authentication != null && authentication.getPrincipal() instanceof OidcUser oidcUser) {
                idToken = oidcUser.getIdToken().getTokenValue();
            }

            String redirectUri = "http://localhost:8081/items";
            String logoutUrl =   "http://localhost:8080/realms/demo-realm/protocol/openid-connect/logout"
                            + "?post_logout_redirect_uri=" + redirectUri
                            + (idToken != null ? "&id_token_hint=" + idToken : "");

            exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
            exchange.getExchange().getResponse()
                    .getHeaders()
                    .setLocation(URI.create(logoutUrl));

            return exchange.getExchange().getResponse().setComplete();
        };
    }

    @Bean
    public SecurityWebFilterChain security(ServerHttpSecurity http, UserEnrichmentFilter userEnrichmentFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.GET, "/items/**").permitAll()
                        .pathMatchers("/cart/**", "/orders/**").authenticated()
                        .anyExchange().permitAll()
                )
                .oauth2Login(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL для инициации logout
                        .logoutHandler(localLogoutHandler()) // Локальный logout
                        .logoutSuccessHandler(compositeLogoutSuccessHandler()) // Редирект в Keycloak
                        .requiresLogout((logoutHandler, exchange) -> true)
                )
                .anonymous(anon -> anon
                        .principal(new UserPrincipal(
                                0L,
                                "anonymous",
                                "anonymous",
                                List.of()
                        ))
                        .authorities("ANONYMOUS")
                )
                .addFilterAfter(
                        userEnrichmentFilter,
                        SecurityWebFiltersOrder.AUTHENTICATION
                )
                .build();
    }
}