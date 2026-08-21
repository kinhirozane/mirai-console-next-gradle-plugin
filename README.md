# mirai-console-next

高效率 QQ 机器人框架（使用 Overflow 的 Onebot 11 协议替换原 Mirai Core 的实现）

## 使用 Gradle 插件

在插件项目的 `build.gradle.kts` 中应用：

```kotlin
plugins {
    id("com.kinhiro.mirai-console-next") version "1.0.0"
}
```

打包插件任务：`./gradlew miraiPluginJar`，产物输出在 `build/mirai/`。

### 环境要求

- Java 25+
- Kotlin 2.4+
- mirai-core-api 2.16.0、mirai-console(-terminal) 2.16.0、overflow-core 1.1.0
  （默认注入版本，可在 `mirai {}` 扩展中用 `coreVersion` / `consoleVersion` / `overflowVersion` 覆盖）

## 使用

- **[启动 Console](https://github.com/mamoe/mirai/blob/dev/mirai-console/docs/Run.md)**

### 安装 JAR 插件

将 `jar` 文件放入 `plugins` 并重启 Mirai Console。

### 执行指令

在控制台输入 `?` 查看可用指令列表。**
注意，请先为用户授予执行某些指令的权限，详见 [示例](https://github.com/mamoe/mirai/blob/dev/mirai-console/docs/BuiltInCommands.md#授予一个用户执行所有指令的权限)**。

### 内置指令

[BuiltInCommands](https://github.com/mamoe/mirai/blob/dev/mirai-console/docs/BuiltInCommands.md#mirai-console---builtin-commands)

## 实用链接

- [社区 SDK](https://github.com/mamoe/mirai#%E4%BD%BF%E7%94%A8-mirai-console-%E6%9C%8D%E5%8A%A1%E7%AB%AF%E4%B8%BA-mirai-console-%E5%BC%80%E5%8F%91%E6%8F%92%E4%BB%B6)
- [论坛](https://mirai.mamoe.net)
- [Mirai 项目组](https://github.com/project-mirai)
- [在 Android 平台使用](https://github.com/mzdluo123/MiraiAndroid)
- Mirai 官方维护的插件:
    - [chat-command](https://github.com/project-mirai/chat-command)
    - [mirai-api-http](https://github.com/project-mirai/mirai-api-http)

## 鸣谢 (Acknowledgements)

- [mirai](https://github.com/mamoe/mirai) — 感谢 mirai 项目提供的 QQ 协议实现、生态与基础 API
- [Overflow](https://github.com/MrXiaoM/Overflow) — 感谢 Overflow 提供的 Onebot 11 协议实现（[开发文档](https://mirai.mrxiaom.top/)）

## 协议与来源 (License & Attribution)

本项目基于 **GNU Affero General Public License v3.0 (AGPL-3.0)** 发布，
完整许可文本见 [LICENSE](LICENSE)。

本仓库部分代码（包括但不限于 `plugin/` 下 Gradle 插件的依赖注入与打包逻辑）
派生自 [mirai-console](https://github.com/mamoe/mirai/tree/dev/mirai-console)。

原项目由 Mamoe Technologies 及贡献者开发，并以 AGPL-3.0 许可发布。
相关源文件中的原始版权声明已保留。

本项目并非 mirai 官方项目，而是基于 mirai-console 部分代码与设计的社区派生项目。

依据 AGPL-3.0，本项目修改版本及再分发版本必须继续以 AGPL-3.0 发布，
并保留相关版权与许可声明。