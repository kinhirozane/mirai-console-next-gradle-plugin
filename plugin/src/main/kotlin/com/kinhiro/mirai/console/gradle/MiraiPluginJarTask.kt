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

import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.work.DisableCachingByDefault
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * 将插件的编译产物与 [GenerateMiraiPluginMetadataTask] 选出的依赖文件合入插件 jar,
 * 并把依赖清单写入 `META-INF/mirai-console-plugin/`。
 */
@DisableCachingByDefault(
    because = "Dependency files and metadata are fed into the jar at execution time by GenerateMiraiPluginMetadataTask"
)
@MiraiConsoleNextDsl
abstract class MiraiPluginJarTask : Jar() {
    internal fun init() {
        dependsOn(MiraiConsoleNextPlugin.MIRAI_PREPARE_METADATA_TASK_NAME)
        archiveExtension.set(MiraiConsoleNextPlugin.FILE_EXTENSION)
        duplicatesStrategy = DuplicatesStrategy.WARN
        dependsOn("compileKotlin")
        from(project.tasks.named<KotlinCompile>("compileKotlin").get().outputs.files)
        exclude { elm -> elm.path.startsWith("META-INF/") && elm.name.endsWith(".SF", true) }
    }
}
