import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default

plugins {
    java
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.2"
}

group = "de.skyslycer"
version = "1.4.5"

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
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://nexuslite.gcnt.net/repos/other/")
}

dependencies {
    implementation(project(":api"))
    implementation(libs.packets)
    implementation(libs.bundles.adventure)
    implementation(libs.configupdater)
    implementation(libs.bundles.lamp)
    implementation(libs.bstats)
    implementation(libs.particles)
    implementation(libs.gui)
    implementation(libs.nbtapi)
    implementation(libs.folialib)
    implementation(libs.configurate) {
        exclude("org.yaml")
    }
    implementation(libs.mclogs) {
        exclude("com.google.code.gson")
    }
    compileOnly(depends.spigot)
    compileOnly(depends.placeholderapi)
    compileOnly(depends.oraxen)
    compileOnly(depends.itemsadder)
    compileOnly(depends.mythicmobs)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    shadowJar {
        relocate("net.kyori", "$shadePattern.kyori")
        relocate("com.bgsoftware.common.config", "$shadePattern.configupdater")
        relocate("revxrsal.commands", "$shadePattern.commands")
        relocate("dev.triumphteam.gui", "$shadePattern.gui")
        relocate("org.spongepowered.configurate", "$shadePattern.config")
        relocate("com.github.retrooper.packetevents", "$shadePattern.packets")
        relocate("io.github.retrooper.packetevents", "$shadePattern.packets.io")
        relocate("org.bstats", "$shadePattern.bstats")
        relocate("com.owen1212055.particlehelper", "$shadePattern.particlehelper")
        relocate("de.tr7zw.changeme.nbtapi", "$shadePattern.nbtapi")
        relocate("kotlin", "$shadePattern.kotlin")
        relocate("gs.mclo.java", "$shadePattern.mclogs")
        relocate("org.intellij", "$shadePattern.annotations")
        relocate("org.jetbrains", "$shadePattern.annotations")
        relocate("io.leangen", "$shadePattern.leangen")
        relocate("com.tcoded.folialib", "$shadePattern.folialib")

        exclude("com/google/**")
        //exclude("assets/mappings/block/**")
        //exclude("assets/mappings/particle/**")
        //exclude("assets/mappings/potion/**")
        exclude("lamp_pt.properties")
        exclude("lamp_it.properties")
        exclude("lamp_fr.properties")
        exclude("lamp-bukkit_pt.properties")
        exclude("lamp-bukkit_it.properties")
        exclude("lamp-bukkit_fr.properties")

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
        minecraftVersion("1.20.4")
    }
}

bukkit {
    main = "de.skyslycer.hmcwraps.HMCWrapsPlugin"
    name = "HMCWraps"
    description = "The best choice to make your items prettier."
    author = "Skyslycer"
    softDepend = listOf("PlaceholderAPI", "ItemsAdder", "Oraxen", "MythicMobs")
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
        register("hmcwraps.commands.wrap.self") {
            description = "Gives access to wrap an item using commands (virtual). The player can only wrap his own tools, not the tools from others."
        }
        register("hmcwraps.commands.unwrap") {
            description = "Gives access to unwrap an item using commands (virtual)."
            children = listOf("hmcwraps.management", "hmcwraps.admin", "hmcwraps.commands.virtual")
        }
        register("hmcwraps.commands.unwrap.self") {
            description = "Gives access to unwrap an item using commands (virtual). The player can only unwrap his own tools, not the tools from others."
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
        register("hmcwraps.debug") {
            description = "Gives access to debug commands."
            children = listOf("hmcwraps.admin")
        }
        register("hmcwraps.commands.create") {
            description = "Gives access to the create command."
            children = listOf("hmcwraps.admin")
        }
        register("hmcwraps.shortcut.disable") {
            description = "If this permission is applied to a player, the shortcut function is disabled only for that player."
        }
        register("hmcwraps.commands.open") {
            description = "Gives access to open the wrap inventory for another player."
            children = listOf("hmcwraps.management", "hmcwraps.admin")
        }
    }
}