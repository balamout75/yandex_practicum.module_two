plugins {
	java
	id("org.springframework.boot") version "4.0.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.yandex.practicum"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-liquibase")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework:spring-jdbc")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.postgresql:r2dbc-postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-data-r2dbc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")
	testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:testcontainers-junit-jupiter")
	testImplementation("org.testcontainers:testcontainers-postgresql")
	testImplementation("org.testcontainers:testcontainers-r2dbc")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
