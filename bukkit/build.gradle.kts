plugins {
    id("com.gradleup.shadow") version "8.3.6"
    java
    eclipse
}

val apiVersion = "1.19"

tasks.named<ProcessResources>("processResources") {
    val props = mapOf(
        "version" to project.version,
        "apiVersion" to apiVersion
    )
    inputs.properties(props)
    filesMatching("plugin.yml") { expand(props) }
}

dependencies {
    compileOnly("org.purpurmc.purpur:purpur-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    implementation("com.github.cryptomorin:XSeries:9.4.0")
    implementation(project(":core"))
}

tasks.shadowJar {
    exclude("io.github.miniplaceholders.**")
    archiveClassifier.set("")
    minimize()
}

tasks.build { dependsOn(tasks.shadowJar) }
tasks.jar  { archiveClassifier.set("part") }

