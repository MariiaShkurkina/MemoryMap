plugins {
    application
    id("java")
    checkstyle
    id("com.github.spotbugs") version "6.4.2"
    pmd

    id("io.freefair.lombok") version "9.0.0"


    id ("org.springframework.boot") version "3.5.7"
    id ("io.spring.dependency-management") version "1.1.7"
}

group = "ru.tbank.education"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation ("com.github.spotbugs:spotbugs-annotations:4.9.6")


    implementation ("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation ("org.springframework.boot:spring-boot-starter-test")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")


    // ✅ JPA + Hibernate
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // (опционально, но полезно)
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.flywaydb:flyway-core:11.20.2")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:11.20.2")

    runtimeOnly("org.postgresql:postgresql")

}


tasks.build {
    dependsOn("check")
}

tasks.test {
    useJUnitPlatform()
}

tasks.check {
    dependsOn(tasks.named("spotbugsMain"))
    dependsOn(tasks.named("spotbugsTest"))
    dependsOn(tasks.named("checkstyleMain"))
    dependsOn(tasks.named("checkstyleTest"))
    dependsOn(tasks.named("pmdMain"))
    dependsOn(tasks.named("pmdTest"))
}


checkstyle {
    toolVersion = "11.1.0"
    isIgnoreFailures = false
    isShowViolations = true
}

tasks.withType<Checkstyle> {
    maxErrors = 0
    maxWarnings = 0
}
