package ru.yandex.practicum.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import ru.yandex.practicum.security.CurrentUserIdArgumentResolver;
import ru.yandex.practicum.service.shoping.UserService;

@Configuration
public class WebConfiguration implements WebFluxConfigurer {

    private final UserService userService;

    public WebConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new CurrentUserIdArgumentResolver(userService));
    }
}