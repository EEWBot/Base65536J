import org.jreleaser.model.Active

plugins {
    id("java")
    `maven-publish`
    signing
    id("org.jreleaser") version "1.17.0"
    id("me.champeau.jmh") version "0.7.3"
}

group = "net.eewbot.base65536j"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.12.2")
}

tasks {
    create<Copy>("includeReadmeAndLicense") {
        destinationDir = project.layout.buildDirectory.file("resources/main").get().asFile

        from(rootProject.file("LICENSE")) {
            rename { "LICENSE_${rootProject.name}" }
        }

        from(rootProject.file("README.md")) {
            rename { "README_${rootProject.name}.md" }
        }
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    jar {
        dependsOn("includeReadmeAndLicense")
    }

    javadoc {
        dependsOn("includeReadmeAndLicense")
    }

    compileTestJava {
        dependsOn("includeReadmeAndLicense")
    }

    assemble {
        dependsOn("sourcesJar")
    }

    test {
        useJUnitPlatform()
    }

    jmh {
        jmhVersion = "1.37"

        warmupIterations = 3
        warmup = "5s"
        iterations = 3
        timeOnIteration = "5s"
        fork = 5

        forceGC = true
        resultFormat = "JSON"
        failOnError = true
    }
}

publishing {
    publications {
        create<MavenPublication>("publication") {
            groupId = rootProject.group.toString()
            artifactId = rootProject.name
            version = rootProject.version.toString()

            from(components.getByName("java"))

            pom {
                name.set(artifactId)
                description.set(rootProject.description)
                url.set("https://github.com/EEWBot/Base65536J")

                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://opensource.org/license/mit")
                    }
                }

                developers {
                    developer {
                        id.set("Siro256")
                        name.set("Siro_256")
                        email.set("siro@siro256.dev")
                        url.set("https://github.com/Siro256")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/EEWBot/Base65536J.git")
                    developerConnection.set("scm:git:ssh://github.com/EEWBot/Base65536J.git")
                    url.set("https://github.com/EEWBot/Base65536J")
                }
            }
        }
    }

    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/EEWBot/Base65536J")
            credentials {
                username = "EEWBot"
                password = System.getenv("GPR_KEY")
            }
        }

        maven {
            name = "Local"
            url = uri(project.layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

jreleaser {
    signing {
        active.set(Active.NEVER)
    }

    release {
        github {
            enabled.set(false)
            token.set("EMPTY")
        }
    }

    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    active.set(Active.ALWAYS)
                    snapshotSupported.set(true)
                    url.set("https://central.sonatype.com/api/v1/publisher")
                    stagingRepository("build/staging-deploy")
                    username.set(System.getenv("MAVEN_CENTRAL_USERNAME"))
                    password.set(System.getenv("MAVEN_CENTRAL_PASSWORD"))
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        System.getenv("SIGNING_KEY_ID"),
        System.getenv("SIGNING_KEY"),
        System.getenv("SIGNING_KEY_PASSWORD")
    )
    sign(publishing.publications.getByName("publication"))
}
