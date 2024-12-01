import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("io.izzel.taboolib") version "2.0.22"
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
}

taboolib {
    env {
        install(
            Metrics,
            CommandHelper,
            Kether,
            JavaScript,
            Bukkit,
            BukkitUI,
            BukkitNMS,
            BukkitNMSUtil,
            BukkitHook,
            BukkitUtil
        )
    }
    description {
        description {
            contributors {
                name("CPJiNan")
            }
            dependencies {
                name("MythicMobs").optional(true)
                name("SX-Attribute").optional(true)
                name("NeigeItems").optional(true)
            }
        }
    }
    version { taboolib = "6.2.1-f095116" }
    relocate("ink.ptms.um", "com.github.cpjinan.plugin.akariitem.um")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v12004:12004:mapped")
    compileOnly("ink.ptms.core:v12004:12004:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    taboo("ink.ptms:um:1.0.1")
}

kotlin {
    sourceSets.all {
        languageSettings {
            languageVersion = "2.0"
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}