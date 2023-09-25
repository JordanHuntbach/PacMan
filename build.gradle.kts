plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.jordan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

application {
    mainClass.set("com.jordan.Game")
}

javafx {
    version = "11"
    modules = listOf("javafx.controls")
}