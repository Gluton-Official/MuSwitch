plugins {
    application
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    id("com.google.devtools.ksp") version "1.8.0-1.0.9"
    id("de.jensklingenberg.ktorfit") version "1.0.0"
}

group = "dev.gluton"
version = "0.0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("me.jakejmattson:DiscordKt:0.23.4") {
        exclude(group = "org.slf4j", module = "slf4j-simple")
    }

    // http
    implementation("io.ktor:ktor-client-core:2.2.3")
    implementation("io.ktor:ktor-client-cio:2.2.3")
    implementation("io.ktor:ktor-client-logging:2.2.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.2.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.3")
    implementation("io.ktor:ktor-client-logging-jvm:2.2.3")
    ksp("de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta17")
    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:1.0.0-beta17")

    // util
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("io.github.url-detector:url-detector:0.1.23")

    // logging
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("org.slf4j:slf4j-api:2.0.6")

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

tasks {
    // https://www.baeldung.com/kotlin/gradle-executable-jar
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources")) // We need this for Gradle optimization to work
        archiveClassifier.set("standalone") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
            sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }
}
