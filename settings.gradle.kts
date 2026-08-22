rootProject.name = "HMCWraps"
include("core", "api")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("depends") {
            library("spigot", "org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
            library("placeholderapi", "me.clip:placeholderapi:2.12.2")
            library("nexo", "com.nexomc:nexo:1.25.0")
            library("oraxen", "io.th0rgal:oraxen:1.217.0")
            library("itemsadder", "com.github.LoneDev6:API-ItemsAdder:3.6.3-beta-14")
            library("mythicmobs", "io.lumine:Mythic-Dist:5.12.0")
            library("annotations", "org.jetbrains:annotations:26.1.0")
            library("executableitems", "com.github.Ssomar-Developement:SCore:5.25.3.9")
            library("zauctionhouse", "com.github.Maxlego08:zAuctionHouseV3-API:3.2.1.9")
            library("auctionguiplus", "com.github.brcdev-minecraft:auctiongui-api:2.1.0")
            library("axauctions", "com.artillexstudios:AxAuctionsAPI:6")
            library("axtrade", "com.artillexstudios:AxTrade:1.24.2")
            library("mmoitems", "net.Indyuce:MMOItems-API:6.10.1-SNAPSHOT")
            library("luckperms", "net.luckperms:api:5.5")

            library("craftengine-core", "net.momirealms", "craft-engine-core").versionRef("craftengine")
            library("craftengine-bukkit", "net.momirealms", "craft-engine-bukkit").versionRef("craftengine")
            version("craftengine", "26.7.2")
            bundle("craftengine", listOf("craftengine-core", "craftengine-bukkit"))
        }
        create("libs") {
            library("particles", "com.owen1212055:particlehelper:1.5.0-SNAPSHOT")
            library("configupdater", "com.github.BG-Software-LLC:CommentedConfiguration:-SNAPSHOT")
            library("bstats", "org.bstats:bstats-bukkit:3.2.1")
            library("gui", "dev.triumphteam:triumph-gui:3.2.0-SNAPSHOT")
            library("configurate", "org.spongepowered:configurate-yaml:4.2.0")
            library("mclogs", "gs.mclo:java:2.2.1")
            library("nbtapi", "de.tr7zw:item-nbt-api:2.15.7")
            library("folialib", "com.tcoded:FoliaLib:0.5.2")

            library("adventure-api", "net.kyori", "adventure-api").versionRef("adventure")
            library("minimessage", "net.kyori", "adventure-text-minimessage").versionRef("adventure")
            library("text-plain", "net.kyori", "adventure-text-serializer-plain").versionRef("adventure")
            library("adventure-bukkit", "net.kyori:adventure-platform-bukkit:4.4.1-granny-SNAPSHOT")
            version("adventure", "4.23.0")
            bundle("adventure", listOf("adventure-api", "minimessage", "adventure-bukkit", "text-plain"))

            library("lamp-common", "io.github.revxrsal", "lamp.common").versionRef("lamp")
            library("lamp-bukkit", "io.github.revxrsal", "lamp.bukkit").versionRef("lamp")
            version("lamp", "4.0.0-rc.17")
            bundle("lamp", listOf("lamp-common", "lamp-bukkit"))
        }
    }
}
