package ru.yandex.practicum.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.service.shoping.UserService;

@Component
public class CurrentUserIdArgumentResolver
        implements HandlerMethodArgumentResolver {

    private final UserService userService;

    public CurrentUserIdArgumentResolver(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class)
                && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Mono<Object> resolveArgument(
            MethodParameter parameter,
            BindingContext bindingContext,
            ServerWebExchange exchange
    ) {

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(this::resolveUserId)
                .defaultIfEmpty(0L)
                .cast(Object.class);
    }

    private Mono<Long> resolveUserId(Authentication auth) {

        if (auth == null) {
            return Mono.just(0L);
        }

        Object principal = auth.getPrincipal();

        // anonymous
        if (principal == null || principal instanceof String) {
            return Mono.just(0L);
        }

        // OIDC
        if (principal instanceof OidcUser oidc) {
            String sub = oidc.getSubject(); // ✅ ВАЖНО
            return userService.findOrCreate(sub)
                    .map(u -> u.getId());
        }

        return Mono.just(0L);
    }
}