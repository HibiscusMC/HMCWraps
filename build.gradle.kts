plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17" apply false
}

allprojects {
    group = "de.skyslycer"
    version = "1.6.3"

    repositories {
        mavenCentral()
        maven("https://repo.skyslycer.de/jitpack")
        maven("https://repo.skyslycer.de/mirrors")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/groups/public")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
        maven("https://repo.bytecode.space/repository/maven-public/")
        maven("https://mvn.lumine.io/repository/maven-public/")
        maven("https://repo.codemc.io/repository/maven-public/")
        maven("https://repo.triumphteam.dev/snapshots")
        maven("https://repo.nexomc.com/releases/")
    }
}

tasks.register("build") {
    group = "build"
    description = "Aggregate task to build all modules"
}

tasks.register<Copy>("copyPluginJar") {
    dependsOn(":core:build")
    val coreProject = project(":core")
    val version = coreProject.version.toString()
    val jarName = "core-$version-all.jar"
    val coreJar = coreProject.layout.buildDirectory.file("libs/$jarName")
    from(coreJar)
    into(layout.buildDirectory.dir("libs"))
    rename { "HMCWraps-$version.jar" }
}

tasks.named("build") {
    dependsOn("copyPluginJar")
}