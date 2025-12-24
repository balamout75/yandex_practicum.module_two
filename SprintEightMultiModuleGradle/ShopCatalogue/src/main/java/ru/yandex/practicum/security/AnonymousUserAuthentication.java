package ru.yandex.practicum.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class AnonymousUserAuthentication extends AbstractAuthenticationToken {

    private final UserPrincipal principal;

    public AnonymousUserAuthentication() {
        super(AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        this.principal = new UserPrincipal(1L);
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return "";
    }
}