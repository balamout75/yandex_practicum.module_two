package ru.yandex.practicum.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.service.shoping.UserService;

import java.util.List;

@Component
public class UserAuthenticationManager implements ReactiveAuthenticationManager {

    private final UserService userService;

    public UserAuthenticationManager(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        // 1️⃣ Anonymous
        if (authentication == null
                || authentication instanceof AnonymousAuthenticationToken) {

            return Mono.just(
                    new UsernamePasswordAuthenticationToken(
                            new UserPrincipal(0L, "anonymous"),
                            null,
                            List.of()
                    )
            );
        }

        // 2️⃣ OAuth2
        if (authentication instanceof OAuth2AuthenticationToken oauth) {

            String sub = ((OidcUser) oauth.getPrincipal()).getSubject();

            return userService.findOrCreate(sub)
                    .map(user ->
                            new UsernamePasswordAuthenticationToken(
                                    new UserPrincipal(user.getId(), sub),
                                    oauth.getCredentials(),
                                    oauth.getAuthorities()
                            )
                    );
        }

        // 3️⃣ Остальные — как есть
        return Mono.just(authentication);
    }
}