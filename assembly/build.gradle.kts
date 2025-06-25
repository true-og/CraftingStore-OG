plugins {
    id("com.gradleup.shadow") version "8.3.6"
    java
    eclipse
}

dependencies {
    implementation(project(":core"))
    implementation(project(":bukkit"))
}

tasks.shadowJar {
    destinationDirectory.set(rootProject.layout.projectDirectory.dir("build/libs"))
    archiveFileName.set("${rootProject.version}.jar")
    archiveClassifier.set("")

    relocate("org.apache.http", "net.craftingstore.libraries.apache.http")
    relocate("org.apache.commons.lang3", "net.craftingstore.libraries.apache.commons.lang3")
    relocate("commons.logging", "net.craftingstore.libraries.apache.commons.logging")
    relocate("org.apache.commons.codec", "net.craftingstore.libraries.apache.commons.codec")
    relocate("org.apache.commons.text", "net.craftingstore.libraries.apache.commons.text")
    relocate("org.json", "net.craftingstore.libraries.json")
    relocate("io.socket", "net.craftingstore.libraries.socket")
    relocate("okhttp3", "net.craftingstore.libraries.okhttp3")
    relocate("okio", "net.craftingstore.libraries.okio")
    relocate("com.cryptomorin.xseries", "net.craftingstore.libraries.xseries")

    exclude("com/cryptomorin/xseries/messages/**")
    exclude("com/cryptomorin/xseries/particles/**")
    exclude("com/cryptomorin/xseries/XBiome*")
    exclude("com/cryptomorin/xseries/NMSExtras*")
    exclude("com/cryptomorin/xseries/NoteBlockMusic*")
    exclude("com/cryptomorin/xseries/XSound*")
    exclude("com/cryptomorin/xseries/XPotion*")
    exclude("com/cryptomorin/xseries/XEnchantment*")
    exclude("com/cryptomorin/xseries/XEntity*")

    minimize()
}

tasks.build {
    dependsOn(tasks.spotlessApply)
    dependsOn(tasks.shadowJar)
}
