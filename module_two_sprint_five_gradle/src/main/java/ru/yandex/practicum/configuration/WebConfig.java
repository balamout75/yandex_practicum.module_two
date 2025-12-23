package ru.yandex.practicum.configuration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.yandex.practicum.mapping.*;

@AutoConfiguration
public class WebConfig implements WebMvcConfigurer {
    // Здесь конфигурация, связанная с контроллерами, ViewResolvers и т. д.

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(ItemToDtoMapper.class)
    public static class ItemMapperConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ItemToDtoMapper itemEntityMapper() {
            return new ItemToDtoMapper();
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(FromCartToOrderMapper.class)
    public static class FromCartToOrderMapperConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public FromCartToOrderMapper fromCartToOrderMapper() {
            return new FromCartToOrderMapper();
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(OrderToDtoMapper.class)
    public static class OrderToDtoMapperConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public OrderToDtoMapper orderToDtoMapper(OrderPositionToDtoMapper orderPositionToDtoMapper) {
            return new OrderToDtoMapper(orderPositionToDtoMapper);
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(OrderPositionToDtoMapper.class)
    public static class OrderPositionToDtoConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public OrderPositionToDtoMapper orderPositionToDto() {
            return new OrderPositionToDtoMapper();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(ConverterRegistrator.class)
    public static class ConverterRegistratorConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ConverterRegistrator converterRegistrator(FormatterRegistry registry) {
            return new ConverterRegistrator(registry);
        }
    }
}