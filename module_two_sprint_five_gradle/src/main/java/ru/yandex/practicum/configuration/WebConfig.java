package ru.yandex.practicum.configuration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.mapping.FromCartToOrderMapper;
import ru.yandex.practicum.mapping.OrderPositionToDtoMapper;
import ru.yandex.practicum.mapping.ItemToDtoMapper;
import ru.yandex.practicum.mapping.OrderToDtoMapper;

@AutoConfiguration
public class WebConfig {
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
    public static class InCartToInOrderMapperConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public FromCartToOrderMapper inCartToInOrderMapper() {
            return new FromCartToOrderMapper();
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(OrderToDtoMapper.class)
    public static class OrderEntityMapperConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public OrderToDtoMapper orderEntityMapper(OrderPositionToDtoMapper orderPositionToDtoMapper) {
            return new OrderToDtoMapper(orderPositionToDtoMapper);
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(OrderPositionToDtoMapper.class)
    public static class InOrderEntityMapperConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public OrderPositionToDtoMapper inOrderEntityMapper() {
            return new OrderPositionToDtoMapper();
        }

    }
}