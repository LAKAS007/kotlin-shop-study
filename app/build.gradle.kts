plugins {
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("plugin.serialization")
}
application { mainClass.set("com.kotlinshop.ApplicationKt") }
val ktorVersion = "3.0.3"
dependencies {
    implementation(project(":domain"))
    implementation(project(":services"))
    implementation(project(":infrastructure"))
    implementation(project(":api"))
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.5.12")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}
tasks.shadowJar {
    archiveClassifier.set("")
    manifest { attributes["Main-Class"] = "com.kotlinshop.ApplicationKt" }
    mergeServiceFiles()
}
