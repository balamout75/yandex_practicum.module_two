package ru.yandex.practicum.configuration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.yandex.practicum.mapping.InCartToInOrderMapper;
import ru.yandex.practicum.mapping.ItemEntityMapper;

@AutoConfiguration
public class WebConfig {
    // Здесь конфигурация, связанная с контроллерами, ViewResolvers и т. д.
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(ItemEntityMapper.class)
    public static class ItemMapperConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ItemEntityMapper itemEntityMapper() {
            return new ItemEntityMapper();
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(InCartToInOrderMapper.class)
    public static class InCartToInOrderMapperConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public InCartToInOrderMapper inCartToInOrderMapper() {
            return new InCartToInOrderMapper();
        }

    }
}