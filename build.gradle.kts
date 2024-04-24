plugins {
    id("java")
}

group = "net.eewbot.base65536j"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:5.10.2")
}

tasks {
    test {
        useJUnitPlatform()
    }
}
