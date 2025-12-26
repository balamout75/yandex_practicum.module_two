package ru.yandex.practicum.security;

import org.springframework.security.core.GrantedAuthority;
import java.security.Principal;
import java.util.Collection;

public class UserPrincipal implements Principal {

    private final Long userId;
    private final String sub;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(
            Long userId,
            String sub,
            String username,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.userId = userId;
        this.sub = sub;
        this.username = username;
        this.authorities = authorities;
    }

    public Long getUserId() {
        return userId;
    }

    public String getSub() {
        return sub;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return username;
    }
}