package ru.yandex.practicum.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.security.AnonymousUserAuthentication;
import ru.yandex.practicum.security.UserPrincipal;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {


    /*@Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .anyExchange().permitAll()
                )
                .anonymous(anonymous -> anonymous
                        .principal(new UserPrincipal(1L))
                )
                .build();
    }

    @Bean
    ReactiveAuthenticationManager anonymousAuthManager() {
        return authentication -> Mono.just(new AnonymousUserAuthentication());
    }*/
}