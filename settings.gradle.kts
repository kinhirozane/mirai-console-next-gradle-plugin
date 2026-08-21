pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

if (JavaVersion.current() < JavaVersion.VERSION_25) throw IllegalStateException("Please use Java 25+!")

rootProject.name = "mirai-console-next"
include("plugin")
