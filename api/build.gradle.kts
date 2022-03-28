plugins {
    java
}

group = "de.skyslycer.hmcwraps"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.jeff-media.de/maven2/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("com.github.oraxen:oraxen:-SNAPSHOT")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:2.5.4")
    compileOnly("com.github.retrooper.packetevents:spigot:2.0-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:4.10.0")
    compileOnly ("net.kyori:adventure-text-minimessage:4.10.0-SNAPSHOT")
    compileOnly("net.kyori:adventure-platform-bukkit:4.0.1")
    compileOnly("com.github.Revxrsal.Lamp:common:2.9.4")
    compileOnly("com.github.Revxrsal.Lamp:bukkit:2.9.4")
    compileOnly("org.bstats:bstats-bukkit:3.0.0")
    compileOnly("dev.triumphteam:triumph-gui:3.1.2")
}