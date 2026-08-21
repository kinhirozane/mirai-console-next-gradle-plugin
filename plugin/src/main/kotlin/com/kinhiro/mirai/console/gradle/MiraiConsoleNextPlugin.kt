/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 * Copyright (C) 2025 Kinhiro Zane and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 * https://github.com/kinhirozane/mirai-console-next/blob/master/LICENSE
 */
package com.kinhiro.mirai.console.gradle

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

abstract class MiraiConsoleNextPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.plugins) {
            apply(JavaPlugin::class)
            apply(MavenPublishPlugin::class)
        }

        with(target.configurations) {
            create(LINKED_CONFIGURATION_NAME).apply { isCanBeResolved = false }
            create(NORMAL_CONFIGURATION_NAME).apply { isCanBeResolved = false }
        }

        MiraiConsoleNextExtension.register(target, target).let { mirai ->
            target.afterEvaluate { project ->
                with(project.extensions) {
                    findByType<JavaPluginExtension>()?.apply {
                        sourceCompatibility = mirai.javaVersion.getOrElse(JavaVersion.VERSION_25)
                        targetCompatibility = mirai.javaVersion.getOrElse(JavaVersion.VERSION_25)
                    }

                    findByType<KotlinJvmProjectExtension>()?.apply {
                        compilerOptions {
                            jvmTarget.set(JvmTarget.fromTarget(mirai.javaVersion.get().toString()))
                            if (mirai.kotlinJvmDefault.getOrElse(false)) {
                                jvmDefault.set(JvmDefaultMode.ENABLE)
                            }
                        }
                    }
                }

                with(project.dependencies) {
                    val coreVersion = mirai.coreVersion.get()
                    val overflowVersion = mirai.overflowVersion.get()
                    val consoleVersion = mirai.consoleVersion.get()

                    if (!mirai.noCoreApi.get()) {
                        add("compileOnly", "net.mamoe:mirai-core-api:$coreVersion")
                        add("testImplementation", "net.mamoe:mirai-core-api:$coreVersion")
                    }

                    if (!mirai.noConsole.get()) {
                        add("compileOnly", "net.mamoe:mirai-console:$consoleVersion")
                        add("testImplementation", "net.mamoe:mirai-console:$consoleVersion")
                    }

                    add("testImplementation", "top.mrxiaom.mirai:overflow-core:$overflowVersion")
                    add("testImplementation", "net.mamoe:mirai-console-terminal:$consoleVersion")
                }

                with(project.tasks) {
                    withType<JavaCompile> {
                        options.encoding = "UTF-8"
                    }

                    val buildPluginTask = register<MiraiPluginJarTask>("miraiPluginJar") {
                        group = "mirai"
                        init()
                        destinationDirectory.value(project.layout.buildDirectory.dir("mirai"))
                    }

                    register(
                        MIRAI_PREPARE_METADATA_TASK_NAME,
                        GenerateMiraiPluginMetadataTask::class,
                        buildPluginTask.get()
                    ).configure { task -> task.dependsOn("compileKotlin") }
                }
            }
        }
    }

    companion object {
        const val FILE_EXTENSION: String = "mirai2.jar"
        const val LINKED_CONFIGURATION_NAME: String = "linked"
        const val NORMAL_CONFIGURATION_NAME: String = "normal"
        const val MIRAI_PREPARE_METADATA_TASK_NAME: String = "miraiPrepareMetadata"
    }
}
