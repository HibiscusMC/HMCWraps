plugins {
    java
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val shadePattern = "$group.shade"

group = "de.skyslycer"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    implementation("net.kyori:adventure-text-minimessage:4.10.0-SNAPSHOT")
    implementation("net.kyori:adventure-platform-bukkit:4.0.1")
    implementation("com.tchristofferson:ConfigUpdater:2.0-SNAPSHOT")
    implementation("com.github.Revxrsal.Lamp:common:2.9.4")
    implementation("com.github.Revxrsal.Lamp:bukkit:2.9.4")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(16))
}

tasks {
    shadowJar {
        relocate("net.kyori.adventure", "$shadePattern.adventure")
        relocate("com.tchristofferson.configupdater", "$shadePattern.configupdater")
        relocate("revxrsal.commands", "$shadePattern.config")
        val nullClassifier: String? = null
        archiveClassifier.set(nullClassifier)
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }
}

bukkit {
    main = "de.skyslycer.hmcwraps.HMCWraps"
    name = "HMCWraps"
    description = "The best choice to make your items prettier."
    authors = listOf("Skyslycer")
    softDepend = listOf("PlaceholderAPI", "ProtocolLib")
}