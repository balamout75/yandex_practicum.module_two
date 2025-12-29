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
import ru.yandex.practicum.dto.shoping.UserDto;
import ru.yandex.practicum.service.shoping.UserService;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Component
public class CurrentUserIdArgumentResolver
        implements HandlerMethodArgumentResolver {

    private final CurrentUserFacade currentUserFacade;

    public CurrentUserIdArgumentResolver(CurrentUserFacade currentUserFacade) {
        this.currentUserFacade = currentUserFacade;
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
        return currentUserFacade.getUserId()
                .cast(Object.class);
    }
}