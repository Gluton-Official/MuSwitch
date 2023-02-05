plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    application
}

group = "dev.gluton"
version = "0.0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("me.jakejmattson:DiscordKt:0.23.4")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("io.github.url-detector:url-detector:0.1.23")
    implementation("com.adamratzman:spotify-api-kotlin-core:3.8.8")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("dev.gluton.muswitch.MainKt")
}
