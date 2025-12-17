//import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
plugins {
	java
	id("org.springframework.boot") version "4.0.0"
	id("io.spring.dependency-management") version "1.1.7"
	//id("org.openapi.generator") version "7.17.0"
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


/*
sourceSets {
	main {
		java {
			srcDir("build/generated/src/main/java")
		}
	}
}


tasks.register<GenerateTask>("buildClient2") {
	generatorName.set("spring")
	inputSpec.set("$projectDir/src/main/resources/api-spec.yaml")
	outputDir.set("$projectDir/build/generated")

	apiPackage.set("ru.yandex.practicum.client.api")
	modelPackage.set("ru.yandex.practicum.client.model")

	configOptions.set(
		mapOf(
			"library" to "spring-http-interface",
			"interfaceOnly" to "true",
			"useJakartaEe" to "true",
			"reactive" to "true",
			"openApiNullable" to "false",
			"useSwaggerAnnotations" to "false"
		)
	)
}
*/
configurations.all {
	exclude(
		group = "com.vaadin.external.google",
		module = "android-json"
	)
}


dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-liquibase")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:3.0.0")
	implementation("com.google.guava:guava:32.1.3-jre")
	implementation("org.springframework:spring-web")

	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.postgresql:r2dbc-postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-r2dbc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")
	testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-redis-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("com.github.codemonstur:embedded-redis:1.4.3")
	testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

	testImplementation("org.testcontainers:testcontainers-junit-jupiter")
	testImplementation("org.testcontainers:testcontainers-postgresql")
	testImplementation("org.testcontainers:testcontainers-r2dbc")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
/*
tasks.named("compileJava") {
	dependsOn("buildClient2")
}
 */