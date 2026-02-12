import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    application
    id("java")
    jacoco
    checkstyle
    id("com.github.spotbugs") version "6.4.2"
    pmd

    id("io.freefair.lombok") version "9.0.0"

    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.tbank.education"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("com.github.spotbugs:spotbugs-annotations:4.9.6")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

    // JPA + Hibernate
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Validation / Security
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    // JWT (jjwt)
    val jjwtVersion = "0.12.5"
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    // Flyway + Postgres
    implementation("org.flywaydb:flyway-core:11.20.2")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:11.20.2")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // Monitoring / Prometheus
    implementation("io.micrometer:micrometer-registry-prometheus")

    testImplementation ("com.h2database:h2")
    testImplementation ("org.assertj:assertj-core")
}

tasks.build {
    dependsOn("check")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.named("jacocoTestReport"))
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }

    // исключаем то, что обычно не имеет смысла в покрытии
    classDirectories.setFrom(
        files(classDirectories.files.map { dir ->
            fileTree(dir) {
                exclude(
                    "**/dto/**",
                    "**/config/**",
                    "**/exception/**",
                    "**/*Application*"
                )
            }
        })
    )
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn(tasks.test)

    violationRules {
        rule {
            element = "BUNDLE"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.50".toBigDecimal()
            }
        }
    }

    classDirectories.setFrom(
        files(classDirectories.files.map { dir ->
            fileTree(dir) {
                exclude(
                    "**/dto/**",
                    "**/config/**",
                    "**/exception/**",
                    "**/*Application*"
                )
            }
        })
    )
}


tasks.check {
    dependsOn(tasks.named("spotbugsMain"))
    dependsOn(tasks.named("spotbugsTest"))
    dependsOn(tasks.named("checkstyleMain"))
    dependsOn(tasks.named("checkstyleTest"))
    dependsOn(tasks.named("pmdMain"))
    dependsOn(tasks.named("pmdTest"))

    // ✅ проверка 50% покрытия входит в check/build
    dependsOn(tasks.named("jacocoTestCoverageVerification"))
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

