package ru.yandex.practicum.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.service.shoping.UserService;

@Component
public class UserEnrichmentFilter implements WebFilter {

    private final UserService userService;

    public UserEnrichmentFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        return ReactiveSecurityContextHolder.getContext()
                .flatMap(ctx -> {
                    Authentication auth = ctx.getAuthentication();

                    if (auth == null || !auth.isAuthenticated()) {
                        return chain.filter(exchange);
                    }

                    if (auth.getPrincipal() instanceof UserPrincipal) {
                        return chain.filter(exchange);
                    }

                    if (!(auth.getPrincipal() instanceof OidcUser oidcUser)) {
                        return chain.filter(exchange);
                    }

                    String sub = oidcUser.getSubject();
                    String username = oidcUser.getPreferredUsername();

                    return userService.findOrCreate(sub)
                            .flatMap(user -> {
                                UserPrincipal principal = new UserPrincipal(
                                        user.getId(),
                                        sub,
                                        username,
                                        auth.getAuthorities()
                                );

                                Authentication enrichedAuth =
                                        new UsernamePasswordAuthenticationToken(
                                                principal,
                                                auth.getCredentials(),
                                                auth.getAuthorities()
                                        );

                                return chain.filter(exchange)
                                        .contextWrite(
                                                ReactiveSecurityContextHolder.withAuthentication(enrichedAuth)
                                        );
                            });
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}