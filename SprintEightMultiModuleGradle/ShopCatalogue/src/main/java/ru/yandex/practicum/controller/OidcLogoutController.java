package ru.yandex.practicum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Controller
public class OidcLogoutController {

    @GetMapping("/oidc/logout")
    public Mono<Void> logout(ServerWebExchange exchange) {

        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders()
                .setLocation(URI.create("/logout"));

        return exchange.getResponse().setComplete();
    }
}