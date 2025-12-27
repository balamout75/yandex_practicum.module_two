package ru.yandex.practicum.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import ru.yandex.practicum.security.CurrentUserFacade;
import ru.yandex.practicum.security.CurrentUserIdArgumentResolver;

@Configuration
public class WebConfiguration implements WebFluxConfigurer {

    private final CurrentUserFacade currentUserFacade;

    public WebConfiguration(CurrentUserFacade currentUserFacade) {
        this.currentUserFacade = currentUserFacade;
    }

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new CurrentUserIdArgumentResolver(currentUserFacade));
    }
}