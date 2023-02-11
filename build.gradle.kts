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
