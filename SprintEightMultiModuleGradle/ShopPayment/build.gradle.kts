//import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
	java
	id("org.springframework.boot") version "4.0.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.openapi.generator") version "7.17.0"
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

openApiGenerate {
	generatorName.set("spring")
	inputSpec.set("$projectDir/src/main/resources/api-spec.yaml")
	outputDir.set("$projectDir/build/generated")
	ignoreFileOverride.set(".openapi-generator-java-sources.ignore")
	modelPackage.set("ru.yandex.practicum.server.model")
	invokerPackage.set("ru.yandex.practicum.server")
	apiPackage.set("ru.yandex.practicum.server.api")
	configOptions.set(mapOf(
		"interfaceOnly" to "true",
		"reactive" to "true",
		"useJakartaEe" to "true",
		"generateSpringApplication" to "false",
		"dateLibrary" to "java8",
		"openApiNullable" to "false"
		//"hideGenerationTimestamp" to "true",
		//"requestMappingMode" to "controller",
		//"interfaceOnly" to "true",
		//"useTags" to "true",
		//"delegatePattern" to "true",
		//"openApiNullable" to "false",
		//"serializableModel" to "true",
		//"returnSuccessCode" to "true",
		//"useSpringBoot3" to "true"
		//"library" to "spring-boot",
	))
}

sourceSets["main"].java.srcDirs("build/generated/src/main/java")

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.apache.httpcomponents.client5:httpclient5:5.5.1")
	implementation("jakarta.validation:jakarta.validation-api:3.1.1")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:3.0.0")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
	testImplementation("org.springframework.security:spring-security-test")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named("compileJava") {
	dependsOn("openApiGenerate")
}