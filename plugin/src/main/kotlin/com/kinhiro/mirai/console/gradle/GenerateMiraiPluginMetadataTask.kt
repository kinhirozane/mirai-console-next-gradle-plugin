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

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.internal.artifacts.ivyservice.DefaultLenientConfiguration
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.component.external.model.ModuleComponentArtifactIdentifier
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(
    because = "Resolves the project's runtimeClasspath and feeds files directly into the jar task"
)
abstract class GenerateMiraiPluginMetadataTask @Inject constructor(
    @JvmField internal val jar: MiraiPluginJarTask
) : DefaultTask() {
    @TaskAction
    fun run() {
        val runtime = mutableSetOf<String>()
        val api = mutableSetOf<String>()
        val linkedDependencies = mutableSetOf<String>()
        val linkToApi = mutableSetOf<String>()
        val shadowedFiles = mutableSetOf<File>()
        val shadowedDependencies = mutableSetOf<String>()
        val subprojects = mutableSetOf<String>()
        val linkedSubprojects = mutableSetOf<String>()

        project.configurations.findByName(MiraiConsoleNextPlugin.LINKED_CONFIGURATION_NAME)
            ?.allDependencies?.forEach { dependency ->
                if (dependency is ExternalModuleDependency) {
                    shadowedDependencies.add("${dependency.group}:${dependency.name}")
                }
            }

        project.configurations.findByName(MiraiConsoleNextPlugin.NORMAL_CONFIGURATION_NAME)
            ?.allDependencies?.forEach { dependency ->
                fun resolve0(dependency: Dependency) {
                    if (dependency is ProjectDependency) {
                        linkedDependencies.add("${dependency.group}:${dependency.name}")
                        linkedSubprojects.add(dependency.path)
                        project.project(dependency.path).configurations.findByName("apiElements")
                            ?.allDependencies?.forEach { resolve0(it) }
                        project.project(dependency.path).configurations.findByName("implementation")
                            ?.allDependencies?.forEach { resolve0(it) }
                    }
                }
                resolve0(dependency)
            }

        fun deepForeachDependencies(configuration: Configuration?, action: (Dependency) -> Unit) {
            (configuration ?: return).allDependencies.forEach { dependency ->
                action(dependency)
                if (dependency is ProjectDependency) {
                    subprojects.add("${dependency.group}:${dependency.name}")
                    deepForeachDependencies(
                        project.project(dependency.path).configurations.findByName(configuration.name),
                        action
                    )
                }
            }
        }

        fun resolveProject(project: Project, doResolveApi: Boolean) {
            deepForeachDependencies(project.configurations.findByName("apiElements")) { dependency ->
                if (dependency is ExternalModuleDependency) {
                    val notation = "${dependency.group}:${dependency.name}"
                    linkedDependencies.add(notation)
                    if (doResolveApi) linkToApi.add(notation)
                }

                if (dependency is ProjectDependency) {
                    subprojects.add("${dependency.group}:${dependency.name}")
                    resolveProject(project.project(dependency.path), doResolveApi)
                }
            }

            project.configurations.findByName("implementation")?.allDependencies?.forEach { dependency ->
                if (dependency is ExternalModuleDependency)
                    linkedDependencies.add("${dependency.group}:${dependency.name}")

                if (dependency is ProjectDependency) {
                    subprojects.add("${dependency.group}:${dependency.name}")
                    resolveProject(project.project(dependency.path), false)
                }
            }
        }

        resolveProject(project, true)
        linkedDependencies.removeAll(shadowedDependencies)
        linkToApi.removeAll(shadowedDependencies)
        linkedDependencies.addAll(miraiDependencies)
        val runtimeClasspath = project.configurations.getByName("runtimeClasspath").resolvedConfiguration

        fun markAsResolved(resolvedDependency: ResolvedDependency) {
            val notation = "${resolvedDependency.moduleGroup}:${resolvedDependency.moduleName}"
            if (notation !in shadowedDependencies) linkedDependencies.add(notation)
            resolvedDependency.children.forEach { dependency -> markAsResolved(dependency) }
        }

        fun linkDependencyTo(resolvedDependency: ResolvedDependency, dependencies: MutableCollection<String>) {
            if (resolvedDependency.allModuleArtifacts.any { a -> a.extension == "jar" }) run link@ {
                val notation = "${resolvedDependency.moduleGroup}:${resolvedDependency.moduleName}"
                if (notation in shadowedDependencies) return@link
                dependencies.add(notation)
            }

            resolvedDependency.children.forEach { dependency -> linkDependencyTo(dependency, dependencies) }
        }

        fun resolveDependency(resolvedDependency: ResolvedDependency) {
            val notation = "${resolvedDependency.moduleGroup}:${resolvedDependency.moduleName}"
            logger.info("Resolving dependency: {}", notation)
            if (notation in linkedDependencies) {
                markAsResolved(resolvedDependency)
                linkDependencyTo(resolvedDependency, runtime)
                if (notation in linkToApi) linkDependencyTo(resolvedDependency, api)
                return
            }

            if (notation in subprojects) {
                resolvedDependency.children.forEach { dependency -> resolveDependency(dependency) }
                return
            }
        }

        runtimeClasspath.firstLevelModuleDependencies.forEach { dependency -> resolveDependency(dependency) }

        logger.info("Resolved linked dependencies               : {}", linkedDependencies)
        logger.info("Resolved link to api dependencies          : {}", linkToApi)
        logger.info("Resolved api dependencies                  : {}", api)
        logger.info("Resolved runtime dependencies              : {}", runtime)
        logger.info("Resolved subprojects dependencies          : {}", subprojects)
        logger.info("Resolved linked subprojects dependencies   : {}", linkedSubprojects)

        val lenientConfiguration = runtimeClasspath.lenientConfiguration
        if (lenientConfiguration is DefaultLenientConfiguration) {
            val resolvedArtifacts = mutableSetOf<ResolvedArtifact>()
            lenientConfiguration.artifacts.forEach { artifact -> resolvedArtifacts.add(artifact) }
            resolvedArtifacts
        } else {
            runtimeClasspath.resolvedArtifacts
        }.forEach { artifact ->
            val artifactId = artifact.id
            if (artifactId is ModuleComponentArtifactIdentifier) {
                val module = artifactId.componentIdentifier
                if ("${module.group}:${module.module}" in linkedDependencies) return@forEach
            }

            val id = artifactId.componentIdentifier
            if (id is ProjectComponentIdentifier) if (id.projectPath in linkedSubprojects) return@forEach
            logger.info("   `- {} - {}", artifactId, artifactId.javaClass)
            shadowedFiles.add(artifact.file)
        }

        shadowedFiles.forEach { file ->
            if (file.isDirectory) jar.from(file)
            else if (file.extension == "jar") jar.from(project.zipTree(file))
            else jar.from(file)
        }

        temporaryDir.also { dir -> dir.mkdirs() }.let { tempDir ->
            tempDir.resolve("api.txt").writeText(api.sorted().joinToString("\n"))
            tempDir.resolve("runtime.txt").writeText(runtime.sorted().joinToString("\n"))
            jar.from(tempDir.resolve("api.txt")) { copy ->
                copy.into("META-INF/mirai-console-plugin")
                copy.rename { "dependencies-shared.txt" }
            }

            jar.from(tempDir.resolve("runtime.txt")) { copy ->
                copy.into("META-INF/mirai-console-plugin")
                copy.rename { "dependencies-private.txt" }
            }
        }
    }

    companion object {
        /**
         * 控制台运行时自带的 mirai 依赖。这些依赖绝不能被打进插件 jar,
         * 也不能出现在 private 依赖清单中。
         */
        val miraiDependencies: MutableSet<String> = mutableSetOf(
            "net.mamoe:mirai-core-api-jvm",
            "net.mamoe:mirai-core-utils-jvm",
            "net.mamoe:mirai-console",
            "net.mamoe:mirai-console-terminal",
            "top.mrxiaom.mirai:overflow-core",
            "top.mrxiaom.mirai:overflow-core-api",
        )
    }
}
