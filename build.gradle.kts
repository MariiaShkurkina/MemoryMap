plugins {
    application
    id("java")
    checkstyle
    id("com.github.spotbugs") version "6.4.2"
    pmd

    id("io.freefair.lombok") version "9.0.0"
}

group = "ru.tbank.education"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation ("com.github.spotbugs:spotbugs-annotations:4.9.6")
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
