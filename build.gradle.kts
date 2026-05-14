plugins {
    id("com.android.application") version "8.13.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
}

val externalBuildRoot = File(
    System.getProperty("user.home"),
    ".gradle/project-builds/suryashaktimain"
)

layout.buildDirectory.set(File(externalBuildRoot, "root"))

subprojects {
    layout.buildDirectory.set(File(externalBuildRoot, path.replace(':', '_').ifBlank { "app" }))
}
