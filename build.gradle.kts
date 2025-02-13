import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    kotlin("jvm") version "2.1.10"
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("com.gradleup.shadow").version("9.0.0-beta7")
}

group = "ru.kainlight.lightcheck"
version = "2.2.5"

val kotlinVersion = "2.1.10"
val adventureVersion = "4.18.0"
val adventureBukkitVersion = "4.3.4"

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":API"))

    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    compileOnly("net.kyori:adventure-api:$adventureVersion")
    compileOnly("net.kyori:adventure-text-minimessage:$adventureVersion")
    compileOnly("net.kyori:adventure-platform-bukkit:$adventureBukkitVersion")

    implementation(files(
        "C:/Users/danny/IdeaProjects/.Kotlin/.private/LightLibrary/bukkit/build/libs/LightLibraryBukkit-PUBLIC-1.0.jar"
    ))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
kotlin {
    jvmToolchain(17)
}

tasks {
    processResources {
        val props = mapOf(
            "pluginVersion" to version,
            "kotlinVersion" to kotlinVersion,
            "adventureVersion" to adventureVersion,
            "adventureBukkitVersion" to adventureBukkitVersion
        )
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    named<ShadowJar>("shadowJar") {
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
}
