import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default

plugins {
    java
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "de.skyslycer"
version = "1.0.0"

val shadePattern = "$group.hmcwraps.shade"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    implementation(project(":api"))
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("com.github.oraxen:oraxen:-SNAPSHOT")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:2.5.4")
    implementation("com.github.retrooper.packetevents:spigot:2.0.0-SNAPSHOT")
    implementation("net.kyori:adventure-api:4.11.0")
    implementation("net.kyori:adventure-text-minimessage:4.11.0")
    implementation("net.kyori:adventure-platform-bukkit:4.1.2")
    implementation("com.tchristofferson:ConfigUpdater:2.0-SNAPSHOT")
    implementation("com.github.Revxrsal.Lamp:common:2.9.4")
    implementation("com.github.Revxrsal.Lamp:bukkit:2.9.4")
    implementation("org.bstats:bstats-bukkit:3.0.0")
    implementation("dev.triumphteam:triumph-gui:3.1.3") {
        exclude("com.google.code.gson")
    }
    implementation("org.spongepowered:configurate-yaml:4.1.2") {
        exclude("org.yaml")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(16))
}

tasks {
    shadowJar {
        relocate("net.kyori", "$shadePattern.kyori")
        relocate("com.tchristofferson.configupdater", "$shadePattern.configupdater")
        relocate("revxrsal.commands", "$shadePattern.commands")
        relocate("dev.triumphteam.gui", "$shadePattern.gui")
        relocate("org.spongepowered.configurate", "$shadePattern.config")
        relocate("com.github.retrooper.packetevents", "$shadePattern.packets")
        relocate("org.bstats", "$shadePattern.bstats")

        exclude("com/google/**")
        exclude("assets/mappings/block/**")
        exclude("assets/mappings/particle/**")
        exclude("assets/mappings/potion/**")

        archiveFileName.set("HMCWraps.jar")
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
    softDepend = listOf("PlaceholderAPI", "ItemsAdder", "Oraxen")
    apiVersion = "1.16"
    permissions {
        register("hmcwraps.admin") {
            description = "Gives access to admin commands."
            default = Default.OP
        }
    }
}