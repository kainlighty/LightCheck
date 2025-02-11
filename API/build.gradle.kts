plugins {
    id("java")
    kotlin("jvm") version "2.0.20"
    id("maven-publish")
}

val apiVersion = "2.2.4"
val artifactName = "api"

group = "ru.kainlight.lightcheck"
version = apiVersion

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io/")
}

dependencies {
    implementation(kotlin("stdlib"))

    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
}

val javaVersion: Int = 17
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
    withSourcesJar()
    withJavadocJar()
}
kotlin {
    jvmToolchain(javaVersion)
}

publishing {
    val gitUrl = "github.com/kainlighty/LightCheck"

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            tasks.kotlinSourcesJar
            tasks.javadoc

            pom {
                artifactId = artifactName
                version = apiVersion

                name.set("LightCheck")
                description.set("To call the player to check the cheats")
                url.set("https://$gitUrl")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://$gitUrl?tab=MIT-1-ov-file#")
                    }
                }

                developers {
                    developer {
                        id.set("kainlight")
                        name.set("Danil Panov")
                        organization.set("kainlighty")
                        url.set("https://github.com/kainlighty")
                    }
                }

                scm {
                    connection.set("scm:git:git://$gitUrl.git")
                    developerConnection.set("scm:git:git@github.com:kainlighty/LightCheck.git")
                    url.set("https://$gitUrl")
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://$gitUrl/issues")
                }
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.$gitUrl")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}