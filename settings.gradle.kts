rootProject.name = "HMCWraps"
include("api")

dependencyResolutionManagement {
    versionCatalogs {
        create("depends") {
            library("spigot", "org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
            library("placeholderapi", "me.clip:placeholderapi:2.11.2")
            library("oraxen", "com.github.oraxen:oraxen:-SNAPSHOT")
            library("itemsadder", "com.github.LoneDev6:API-ItemsAdder:3.2.5")
            library("mythicmobs", "io.lumine:Mythic-Dist:5.2.1")
            library("annotations", "org.jetbrains:annotations:24.0.1")
        }
        create("libs") {
            library("packets", "com.github.retrooper.packetevents:spigot:2.0.0-SNAPSHOT")
            library("particles", "com.owen1212055:particlehelper:1.1.0-SNAPSHOT")
            library("configupdater", "com.tchristofferson:ConfigUpdater:2.0-SNAPSHOT")
            library("bstats", "org.bstats:bstats-bukkit:3.0.1")
            library("gui", "dev.triumphteam:triumph-gui:3.1.5")
            library("configurate", "org.spongepowered:configurate-yaml:4.1.2")
            library("mclogs", "com.github.aternosorg:mclogs-java:v2.2.0")
            library("nbtapi", "de.tr7zw:item-nbt-api:2.11.1")

            library("adventure-api", "net.kyori", "adventure-api").versionRef("adventure")
            library("minimessage", "net.kyori", "adventure-text-minimessage").versionRef("adventure")
            library("adventure-bukkit", "net.kyori:adventure-platform-bukkit:4.3.0")
            version("adventure", "4.12.0")
            bundle("adventure", listOf("adventure-api", "minimessage", "adventure-bukkit"))

            library("lamp-common", "com.github.Revxrsal.Lamp", "common").versionRef("lamp")
            library("lamp-bukkit", "com.github.Revxrsal.Lamp", "bukkit").versionRef("lamp")
            version("lamp", "3.1.1")
            bundle("lamp", listOf("lamp-common", "lamp-bukkit"))
        }
    }
}
