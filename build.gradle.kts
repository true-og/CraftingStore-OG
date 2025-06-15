// CraftingStore-OG/build.gradle.kts
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec

plugins {
	eclipse
	id("com.gradleup.shadow") version "8.3.6" apply false
}

group = "net.craftingstore"
version = System.getenv("GITHUB_VERSION") ?: "CraftingStore-OG-1.1"

subprojects {

    pluginManager.apply("java")
    pluginManager.apply("eclipse")

    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
            vendor.set(JvmVendorSpec.GRAAL_VM)
        }
    }

    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.purpurmc.org/snapshots")
        maven("https://jitpack.io")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://rayzr.dev/repo/")
        maven("https://repo.codemc.org/repository/maven-public")
    }

    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf("-parameters", "-Xlint:deprecation"))
        options.encoding = "UTF-8"
        options.isFork = true
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}

