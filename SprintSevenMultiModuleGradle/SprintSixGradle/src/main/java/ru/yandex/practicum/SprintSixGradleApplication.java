package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SprintSixGradleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SprintSixGradleApplication.class, args);
	}

}
