package ru.yandex.practicum.SprintSixGradle;

import org.springframework.boot.SpringApplication;
import ru.yandex.practicum.SprintSixGradleApplication;

public class TestSprintSixGradleApplication {

	public static void main(String[] args) {
		SpringApplication.from(SprintSixGradleApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
