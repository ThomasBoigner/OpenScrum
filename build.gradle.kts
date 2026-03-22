plugins {
    kotlin("jvm") version libs.versions.kotlin.reflect
    kotlin("plugin.spring") version libs.versions.kotlin.reflect
    id("org.springframework.boot") version libs.versions.spring.boot
    id("io.spring.dependency-management") version libs.versions.spring.dependency.management
    id("org.jlleitschuh.gradle.ktlint") version libs.versions.ktlint
}

group = "at.fhtw"
version = "0.0.1-SNAPSHOT"
description = "OpenScrum"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom(
            libs.spring.modulith.bom
                .get()
                .toString(),
        )
    }
}

dependencies {
    configurations.all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }

    // kotlin
    implementation(libs.kotlin.reflect)
    // spring boot
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    developmentOnly(libs.spring.boot.devtools)
    implementation(libs.spring.boot.starter.log4j2)
    // spring modulith
    implementation(libs.spring.modulith.core)
    implementation(libs.spring.modulith.starter.jpa)
    // spring security
    implementation(libs.spring.boot.starter.security)
    // thymeleaf
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.htmx.spring.boot.thymeleaf)
    implementation(libs.htmx.org)
    // database
    implementation(libs.h2)
    implementation(libs.postgresql)
    // testing
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    implementation(libs.selenium.java)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
