package ru.yandex.practicum.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.shoping.UserDto;
import ru.yandex.practicum.service.shoping.UserService;

@Service
public class CurrentUserFacade {

    private final UserService userService;
    private static final UserDto ANONYMOUS =new UserDto(0L, "Anonymous", "User", "anonymous");

    public CurrentUserFacade(UserService userService) {
        this.userService = userService;
    }

    /** Для сервисов */
    public Mono<Long> getUserId() {
        return getCurrentUser().map(UserDto::id);
    }

    /** Если нужны данные пользователя */
    public Mono<UserDto> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(this::resolveUser)
                .defaultIfEmpty(ANONYMOUS);
    }

    private Mono<UserDto> resolveUser(Authentication auth) {

        // anonymous / no security context
        if (auth == null || !auth.isAuthenticated()) {
            return Mono.just(ANONYMOUS);
        }

        Object principal = auth.getPrincipal();

        // Spring Security anonymous
        if (principal instanceof String) {
            return Mono.just(ANONYMOUS);
        }

        // Frontend (authorization_code)
        if (principal instanceof OidcUser oidc) {
            return userService.findOrCreate(oidc.getSubject());
        }

        return Mono.error(new IllegalStateException(
                "Unsupported principal type: " + principal.getClass()
        ));
    }
}