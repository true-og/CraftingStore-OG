plugins {
    id("java") // Tell gradle this is a java project.
    id("java-library") // Import helper for source-based libraries.
    id("com.gradleup.shadow") version "8.3.6" // Import shadow API.
    eclipse // Import eclipse plugin for IDE integration.
}

dependencies {
    api("org.apache.commons:commons-text:1.10.0")

    implementation("com.github.thijsa:socket.io-client-java:1.0.3")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-logging:commons-logging:1.2")

    compileOnly("com.google.code.gson:gson:2.8.9")
}

tasks.build { dependsOn(tasks.spotlessApply) }
