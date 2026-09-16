import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    id("io.spring.dependency-management") version "1.1.7"
}

group = "es.unizar.webeng"
version = "2026-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

extra.set("springCloudVersion", "2023.0.3")

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${extra.get("springCloudVersion")}")
    }
}

dependencies {
    val springBootVersion = libs.versions.springBoot.get()
    implementation(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    testImplementation(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    developmentOnly(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
 
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.bootstrap)
    implementation(libs.webjars.locator.lite)
    runtimeOnly(libs.kotlin.reflect)
    developmentOnly(libs.spring.boot.devtools)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.spring.boot.restclient)
    testImplementation(libs.spring.boot.resttestclient)

    implementation("com.bucket4j:bucket4j-core:8.10.1")

    implementation("org.springframework.cloud:spring-cloud-starter-gateway-mvc")
    implementation("com.bucket4j:bucket4j-caffeine:8.10.1")
    implementation("com.github.ben-manes.caffeine:caffeine")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<BootRun> {
    sourceResources(sourceSets["main"])
}