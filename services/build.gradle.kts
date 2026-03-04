dependencies {
    implementation(project(":domain"))
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("ch.qos.logback:logback-classic:1.5.12")
    testImplementation("io.mockk:mockk:1.13.14")
    testImplementation(kotlin("test"))
}
