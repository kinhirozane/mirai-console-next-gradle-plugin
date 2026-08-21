import com.diffplug.spotless.LineEnding
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-gradle-plugin`
    alias(libs.plugins.spotless)
    alias(libs.plugins.plugin.publish)
    `maven-publish`
    idea
}

group = "com.kinhiro.mirai"
version = "1.0.0"
description = "Gradle plugin for Mirai Console Next"

base {
    archivesName = "mirai-console-next-gradle-plugin"
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }

    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(gradleKotlinDsl())
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.kotlin.gradle.plugin.api)
    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    website = "https://github.com/kinhirozane/mirai-console-next"
    vcsUrl = "https://github.com/kinhirozane/mirai-console-next.git"
    plugins {
        create("mirai-console-next") {
            id = "com.kinhiro.mirai-console-next"
            implementationClass = "com.kinhiro.mirai.console.gradle.MiraiConsoleNextPlugin"
            displayName = "Mirai Console Next"
            description = project.description
            tags = listOf("kotlin", "framework", "mirai", "overflow")
        }
    }
}

spotless {
    kotlin {
        target("**/*.kt")
        licenseHeaderFile(rootProject.file("spotless/license-header.txt"), "^(@file:|package)")
        lineEndings = LineEnding.UNIX
        encoding("UTF-8")
        endWithNewline()
    }
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        if (name == "pluginMaven") {
            artifactId = "mirai-console-next-gradle-plugin"
        }
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
