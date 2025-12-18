package ru.yandex.practicum.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@ComponentScan(
    basePackages = {"ru.yandex.practicum.server", "ru.yandex.practicum.server.api"},
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class ShopPaymentService {

    public static void main(String[] args) {
        SpringApplication.run(ShopPaymentService.class, args);
    }


}