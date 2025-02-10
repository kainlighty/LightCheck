plugins {
    id("java")
    kotlin("jvm") version "2.0.20"
    id("maven-publish")
}

group = "ru.kainlight.lightcheck"
version = "2.2.4"

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io/")
}

dependencies {
    implementation(kotlin("stdlib"))

    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withSourcesJar()
    withJavadocJar()
}
kotlin {
    jvmToolchain(17)
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
}

tasks.jar {
    archiveBaseName.set("API")
    archiveVersion.set("${project.version}")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
}

tasks.register<Task>("deploy") {
    val DEPLOY_DIR = project.findProperty("deployDir") as String
    doLast {
        copy {
            from(layout.buildDirectory.dir("libs"))
            into(layout.projectDirectory.dir("$DEPLOY_DIR/ru/kainlight/LightCheck/${project.version}"))
        }
        exec {
            workingDir = project.rootDir
            commandLine = listOf("./gradlew", "publish")
        }
    }
}