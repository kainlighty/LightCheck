import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    kotlin("jvm") version "2.0.20"
    `maven-publish`

    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("com.github.johnrengelman.shadow").version("8.1.1")
}

group = "ru.kainlight.lightcheck"
version = "2.2.3"

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io/")
}

dependencies {
    implementation(kotlin("stdlib"))

    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.4")

    implementation(files(
        "C:/Users/danny/IdeaProjects/.Kotlin/.private/LightLibrary/bukkit/build/libs/LightLibraryBukkit-PUBLIC-1.0.jar"
    ))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("LightCheck")
                description.set("To call the player to check the cheats")
                url.set("https://github.com/kainlighty/LightCheck")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/kainlighty/LightCheck?tab=MIT-1-ov-file#")
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
                    connection.set("scm:git:git://github.com/kainlighty/LightCheck.git")
                    developerConnection.set("scm:git:git@github.com:kainlighty/LightCheck.git")
                    url.set("https://github.com/kainlighty/LightCheck")
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/kainlighty/LightCheck/issues")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/kainlighty/LightCheck")
        }
    }
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
kotlin {
    jvmToolchain(17)
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set(project.name)
        archiveFileName.set("${project.name}-${project.version}.jar")

        // Исключения и переименование пакетов
        exclude("META-INF/maven/**")
        exclude("META-INF/INFO_BIN")
        exclude("META-INF/INFO_SRC")
        //exclude("kotlin")
        //exclude("org/jetbrains/kotlin/**")

        val shadedPath = "ru.kainlight.lightcheck.shaded"

        relocate("ru.kainlight.lightlibrary", "$shadedPath.lightlibrary")
    }
}

