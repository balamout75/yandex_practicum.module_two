package ru.yandex.practicum.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.shoping.UserDto;
import ru.yandex.practicum.service.shoping.UserService;

@Service
public class CurrentUserFacade {

    private final UserService userService;

    public CurrentUserFacade(UserService userService) {
        this.userService = userService;
    }

    /** Основной метод для сервисов */
    public Mono<Long> getUserId() {
        return getCurrentUser().map(UserDto::id);
    }

    /** Можно использовать, если нужны данные пользователя */
    public Mono<UserDto> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .flatMap(this::resolveUser)
                .switchIfEmpty(Mono.error(new IllegalStateException("Unauthenticated")));
    }

    private Mono<UserDto> resolveUser(Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            return Mono.error(new IllegalStateException("Unauthenticated"));
        }
        Object principal = auth.getPrincipal();
        // Frontend (authorization_code)
        if (principal instanceof OidcUser oidc) {
            return userService.findOrCreate(oidc.getSubject());
        }

        return Mono.error(new IllegalStateException(
                "Unsupported principal type: " + principal.getClass()
        ));
    }
}