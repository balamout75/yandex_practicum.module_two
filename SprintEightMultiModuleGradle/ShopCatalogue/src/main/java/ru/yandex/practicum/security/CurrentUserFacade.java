package ru.yandex.practicum.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.shoping.UserDto;
import ru.yandex.practicum.service.shoping.UserService;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Service
public class CurrentUserFacade {

    private final UserService userService;
    private static final UserDto ANONYMOUS =new UserDto(0L, "Anonymous", "User", "anonymous");

    public CurrentUserFacade(UserService userService) {
        this.userService = userService;
    }

    /** Для сервисов */
    public Mono<Long> getUserId() {
        return getCurrentUser()
                .map(UserDto::id)
                .defaultIfEmpty(0L);
    }

    /** Если нужны данные пользователя */
    public Mono<UserDto> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(this::resolveUser)
                .switchIfEmpty(Mono.just(ANONYMOUS));
    }

    private Mono<UserDto> resolveUser(Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            return Mono.empty();
        }

        if (!(auth instanceof OAuth2AuthenticationToken token)) {
            return Mono.empty();
        }

        if (!(token.getPrincipal() instanceof OidcUser oidc)) {
            return Mono.empty();
        }

        return userService.findOrCreate(oidc.getSubject());
    }
}