import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default

plugins {
    java
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "2.0.1"
}

group = "de.skyslycer"
version = "1.2.0"

val shadePattern = "$group.hmcwraps.shade"

repositories {
    mavenCentral()
    maven("https://repo.skyslycer.de/jitpack")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.bytecode.space/repository/maven-public/")
}

dependencies {
    implementation(project(":api"))
    implementation("com.github.retrooper.packetevents:spigot:2.0.0-SNAPSHOT")
    implementation("net.kyori:adventure-api:4.12.0")
    implementation("net.kyori:adventure-text-minimessage:4.12.0")
    implementation("net.kyori:adventure-platform-bukkit:4.2.0")
    implementation("com.tchristofferson:ConfigUpdater:2.0-SNAPSHOT")
    implementation("com.github.Revxrsal.Lamp:common:3.1.1")
    implementation("com.github.Revxrsal.Lamp:bukkit:3.1.1")
    implementation("org.bstats:bstats-bukkit:3.0.0")
    implementation("com.owen1212055:particlehelper:1.1.0-SNAPSHOT")
    implementation("dev.triumphteam:triumph-gui:3.1.4") {
        exclude("com.google.code.gson")
    }
    implementation("org.spongepowered:configurate-yaml:4.1.2") {
        exclude("org.yaml")
    }
    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("com.github.oraxen:oraxen:-SNAPSHOT")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.2.5")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
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
        relocate("com.owen1212055.particlehelper", "$shadePattern.particlehelper")

        exclude("com/google/**")
        exclude("assets/mappings/block/**")
        exclude("assets/mappings/particle/**")
        exclude("assets/mappings/potion/**")

        archiveClassifier.set("")
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.compilerArgs.add("-parameters")
        options.encoding = "UTF-8"
        options.isFork = true
    }

    runServer {
        minecraftVersion("1.19.3")
    }
}

bukkit {
    main = "de.skyslycer.hmcwraps.HMCWraps"
    name = "HMCWraps"
    description = "The best choice to make your items prettier."
    authors = listOf("Skyslycer")
    softDepend = listOf("PlaceholderAPI", "ItemsAdder", "Oraxen")
    apiVersion = "1.17"
    permissions {
        register("hmcwraps.admin") {
            description = "Gives access to all commands."
            default = Default.OP
        }
        register("hmcwraps.management") {
            description = "Gives access to every command except the reload and the convert command."
        }
        register("hmcwraps.commands.physical") {
            description = "Gives access to all commands related to physical wraps."
        }
        register("hmcwraps.commands.virtual") {
            description = "Gives access to all commands related to virtual wraps."
        }
        register("hmcwraps.commands.reload") {
            description = "Gives access to the reload command."
            children = listOf("hmcwraps.admin")
        }
        register("hmcwraps.commands.convert") {
            description = "Gives access to the convert command."
            children = listOf("hmcwraps.admin")
        }
        register("hmcwraps.commands.wrap") {
            description = "Gives access to wrap an item using commands (virtual)."
            children = listOf("hmcwraps.management", "hmcwraps.admin", "hmcwraps.commands.virtual")
        }
        register("hmcwraps.commands.unwrap") {
            description = "Gives access to unwrap an item using commands (virtual)."
            children = listOf("hmcwraps.management", "hmcwraps.admin", "hmcwraps.commands.virtual")
        }
        register("hmcwraps.commands.give.wrapper") {
            description = "Gives access to giving physical wrappers to players."
            children = listOf("hmcwraps.management", "hmcwraps.admin", "hmcwraps.commands.physical")
        }
        register("hmcwraps.commands.give.unwrapper") {
            description = "Gives access to giving physical unwrappers to players."
            children = listOf("hmcwraps.management", "hmcwraps.admin", "hmcwraps.commands.physical")
        }
        register("hmcwraps.commands.preview") {
            description = "Gives access to starting a preview on behalf of others."
            children = listOf("hmcwraps.management", "hmcwraps.admin")
        }
        register("hmcwraps.commands.list") {
            description = "Gives access to the list command."
            children = listOf("hmcwraps.management", "hmcwraps.admin")
        }
        register("hmcwraps.wraps") {
            description = "Gives access to the wraps inventory (has to be enabled in config)."
            children = listOf("hmcwraps.management", "hmcwraps.admin")
        }
    }
}