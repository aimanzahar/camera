plugins {
    java
    id("com.gradleup.shadow") version "9.0.0-beta12"
}

group = property("group") as String
version = property("version") as String

val hytaleHome: String = findProperty("hytale_home") as String?
    ?: "${System.getProperty("user.home")}/AppData/Roaming/Hytale"
val patchline: String = findProperty("patchline") as String? ?: "release"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // Hytale Server API — uses installed game if available, falls back to libs/
    val hytaleServerJar = file("$hytaleHome/install/$patchline/package/game/latest/Server/HytaleServer.jar")
    val localServerJar = file("./libs/HytaleServer.jar")
    when {
        hytaleServerJar.exists() -> compileOnly(files(hytaleServerJar))
        localServerJar.exists() -> compileOnly(files(localServerJar))
        else -> {
            logger.warn("WARNING: HytaleServer.jar not found. Looked in:")
            logger.warn("  - $hytaleServerJar")
            logger.warn("  - ${localServerJar.absolutePath}")
            logger.warn("Build will fail at compile time. Install Hytale or place HytaleServer.jar in libs/")
            compileOnly(files(localServerJar))
        }
    }
    implementation("com.google.code.gson:gson:2.11.0")
}

tasks.compileJava {
    options.encoding = Charsets.UTF_8.name()
}

tasks.shadowJar {
    archiveClassifier.set("")
    archiveBaseName.set("RTSCamera")
    minimize()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
