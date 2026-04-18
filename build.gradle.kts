import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    kotlin("jvm") version "2.3.10"
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("com.gradleup.shadow") version "9.4.1"
}

group = "ru.kainlight.lightcheck"
version = "2.2.6"

val kotlinVersion = "2.3.10"

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":API"))

    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    implementation(files(
        "C:/Users/kainlight/IdeaProjects/LightLibrary/bukkit/build/libs/LightLibraryBukkit-1.0.jar"
    ))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
kotlin {
    jvmToolchain(21)
}

tasks {
    processResources {
        val props = mapOf(
            "pluginVersion" to version,
            "kotlinVersion" to kotlinVersion,
        )
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    val shadowJar = named<ShadowJar>("shadowJar") {
        archiveBaseName.set(project.name)
        archiveFileName.set("${project.name}-${project.version}.jar")

        // Исключения
        exclude("META-INF/maven/**",
                "META-INF/INFO_BIN",
                "META-INF/INFO_SRC",
                "kotlin/**"
        )
        mergeServiceFiles()

        // Переименование пакетов
        val shadedPath = "ru.kainlight.lightcheck.shaded"
        relocate("ru.kainlight.lightlibrary", "$shadedPath.lightlibrary")
    }

    "build" {
        dependsOn(shadowJar)
    }
}
