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
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

/**
 * ```
 * mirai {
 *   // 配置
 * }
 * ```
 */
@MiraiConsoleNextDsl
abstract class MiraiConsoleNextExtension @Inject constructor(objects: ObjectFactory) : ExtensionAware {
    /**
     * Java 和 Kotlin 编译目标。
     *
     * 默认: [JavaVersion.VERSION_25]
     */
    val javaVersion: Property<JavaVersion> = objects.property<JavaVersion>().convention(JavaVersion.VERSION_25)

    /**
     * 为 `true` 时配置 Kotlin 编译器参数 "-Xjvm-default=enable"。
     *
     * 默认: `false`
     */
    val kotlinJvmDefault: Property<Boolean> = objects.property<Boolean>().convention(false)

    /**
     * 为 `true` 时不自动添加 mirai-core-api 的依赖。
     *
     * 默认: `false`
     */
    val noCoreApi: Property<Boolean> = objects.property<Boolean>().convention(false)

    /**
     * 为 `true` 时不自动添加 mirai-console 的依赖。
     *
     * 默认: `false`
     */
    val noConsole: Property<Boolean> = objects.property<Boolean>().convention(false)

    /**
     * 自动添加的 mirai-core 和 mirai-core-api 的版本。
     *
     * 默认: [VersionConstants.CORE_VERSION]
     */
    val coreVersion: Property<String> = objects.property<String>().convention(VersionConstants.CORE_VERSION)

    /**
     * 自动添加的 `top.mrxiaom.mirai:overflow-core` 的版本。
     *
     * 默认: [VersionConstants.OVERFLOW_VERSION]
     */
    val overflowVersion: Property<String> = objects.property<String>().convention(VersionConstants.OVERFLOW_VERSION)

    /**
     * 自动添加的 mirai-console 后端和前端的版本。
     *
     * 默认: [VersionConstants.CONSOLE_VERSION]
     */
    val consoleVersion: Property<String> = objects.property<String>().convention(VersionConstants.CONSOLE_VERSION)

    companion object {
        fun register(project: Project, extension: Any): MiraiConsoleNextExtension = extension.configureExtension(
            "mirai"
        )
    }
}
