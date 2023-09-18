plugins {
    kotlin("jvm") version("1.8.0")

    id("java")

    `java-library`
    `maven-publish`
    id("com.github.weave-mc.weave") version("8b70bcc707")
    id("com.github.johnrengelman.shadow") version("6.1.0")
}

group = "xyz.flapjack"
version = "1.2"

minecraft.version("1.8.9")

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.spongepowered.org/maven/")
}

dependencies {
    compileOnly("com.github.weave-mc:weave-loader:70bd82f")
    compileOnly("org.spongepowered:mixin:0.8.5")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.compileJava {
    options.release.set(17)
}

tasks.jar {
    manifest.attributes(
        "Main-Class" to "xyz.flapjack.container.Container",
    )

    /**
     * This is for development purposes and will compile the mod straight to your mods' folder.
     *
     * destinationDirectory.set(file("${System.getProperty("user.home")}/.lunarclient/mods"))
     *
     */
}