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

/**
 * 插件默认注入的 mirai 构件版本，可通过 [MiraiConsoleNextExtension.coreVersion] /
 * [MiraiConsoleNextExtension.overflowVersion] / [MiraiConsoleNextExtension.consoleVersion] 覆盖。
 */
internal object VersionConstants {
    /** `net.mamoe:mirai-core-api` 的版本 */
    const val CORE_VERSION: String = "2.16.0"

    /** `top.mrxiaom.mirai:overflow-core` 的版本（mirai-core 实现的替代品） */
    const val OVERFLOW_VERSION: String = "1.1.0"

    /** `net.mamoe:mirai-console` 与 `net.mamoe:mirai-console-terminal` 的版本 */
    const val CONSOLE_VERSION: String = "2.16.0"
}
