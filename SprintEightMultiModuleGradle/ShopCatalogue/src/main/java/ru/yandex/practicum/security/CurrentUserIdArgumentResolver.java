package ru.yandex.practicum.security;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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