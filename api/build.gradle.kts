plugins {
    java
    `maven-publish`
    kotlin("jvm") version "2.0.20"
    id("org.jetbrains.dokka") version ("1.9.20")
}

group = "de.skyslycer.hmcwraps"
version = rootProject.version

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
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.triumphteam.dev/snapshots")
    maven("https://repo.nexomc.com/snapshots/")
}

dependencies {
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:2.0.0")
    compileOnly(depends.spigot)
    compileOnly(depends.annotations)
    compileOnly(depends.placeholderapi)
    compileOnly(libs.packets)
    compileOnly(libs.gui)
    compileOnly(libs.bundles.adventure)
    compileOnly(libs.configurate)
    compileOnly(libs.nbtapi)
    compileOnly(libs.folialib)
}

tasks {
    dokkaHtml {
        moduleName.set("HMCWraps API")
    }

    compileJava {
        options.compilerArgs.add("-parameters")
        options.encoding = "UTF-8"
        options.isFork = true
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

publishing {
    val publishData = PublishData(project)
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        groupId = project.group as String?
        artifactId = project.name
        version = publishData.getVersion()
    }

    repositories {
        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("REPO_USERNAME")
                    password = System.getenv("REPO_PASSWORD")
                }
            }

            name = "HibiscusMC"
            url = uri(publishData.getRepository())
        }
    }
}

class PublishData(private val project: Project) {
    var type: Type = getReleaseType()
    var hashLength: Int = 7

    private fun getReleaseType(): Type {
        val branch = getCheckedOutBranch()
        return when {
            branch.contentEquals("master") || branch.contentEquals("local") -> Type.RELEASE
            branch.startsWith("dev") -> Type.DEV
            else -> Type.SNAPSHOT
        }
    }

    private fun getCheckedOutGitCommitHash(): String =
        System.getenv("GITHUB_SHA")?.substring(0, hashLength) ?: "local"

    private fun getCheckedOutBranch(): String =
        System.getenv("GITHUB_REF")?.replace("refs/heads/", "") ?: "local"

    fun getVersion(): String = getVersion(false)

    fun getVersion(appendCommit: Boolean): String =
        type.append(getVersionString(), appendCommit, getCheckedOutGitCommitHash())

    private fun getVersionString(): String =
        (project.version as String).replace("-SNAPSHOT", "").replace("-DEV", "")

    fun getRepository(): String = type.repo

    enum class Type(private val append: String, val repo: String, private val addCommit: Boolean) {
        RELEASE("", "https://repo.hibiscusmc.com/releases/", false),
        DEV("-DEV", "https://repo.hibiscusmc.come/snapshots/", true),
        SNAPSHOT("-SNAPSHOT", "https://repo.hibiscusmc.com/snapshots/", true);

        fun append(name: String, appendCommit: Boolean, commitHash: String): String =
            name.plus(append).plus(if (appendCommit && addCommit) "-".plus(commitHash) else "")
    }
}