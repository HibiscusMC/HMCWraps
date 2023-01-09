<p align="center">
    <a href="https://polymart.org/resource/hmcwraps.3216">
        <img width="300" alt="logo" src="https://upload.skyslycer.de/wraps_logo_resized.png"/>
    </a>
</p>

<h2 align="center">The best choice to make your items prettier.</h4>

<p align="center">
    <a href="https://www.spigotmc.org/resources/hmcwraps.107099/6">
        <img alt="polymart" src="https://img.shields.io/badge/SPIGOT-HMCWraps-brightgreen?style=for-the-badge"/>
    </a>
    <a href="https://polymart.org/resource/hmcwraps.3216">
        <img alt="polymart" src="https://img.shields.io/badge/POLYMART-HMCWraps-brightgreen?style=for-the-badge"/>
    </a>
    <a href="https://docs.hibiscusmc.com/">
        <img alt="docs" src="https://img.shields.io/badge/Documentation-brightgreen?style=for-the-badge"/>
    </a>
    <a href="https://discord.gg/pcm8kWrdNt">
        <img alt="discord" src="https://img.shields.io/badge/Discord Support-blue?style=for-the-badge"/>
    </a>
</p>

Say hello to the latest plugin entry, **HMCWraps**, in the HCS plugin family!

HMCWraps is an easy-to-use, intuitive item cosmetic plugin which is completely EULA compliant providing cool features.

## What does it do?
**HMCWraps** basically wraps and unwraps items by applying configured **custom model** data and **color**.

As the plugin doesn't change anything else about the item itself, it does not provide an advantage over other players,
as color and look of an item are purely cosmetic.

The configurations that are put onto the item are called wraps, and conveniently so, you can call the according setting and removing of a wrap **wrapping** and **unwrapping**.

## How to get it
Polymart: https://polymart.org/resource/hmcwraps.3216

Spigot: `Coming soon!`

## Wiki & Support
The wiki can be found here: https://docs.hibiscusmc.com/hmcwraps/setup

You can join our Discord server for feedback, support and reports here: https://discord.gg/pcm8kWrdNt

## Using in your plugin
HMCWraps has a nice API everybody is able to use.

Consult the wiki for a simple example: https://docs.hibiscusmc.com/hmcwraps/api

If you need more, just check the JavaDocs: https://hibiscusmc.github.io/HMCWraps/

For any other questions regarding the API, join our Discord server: https://discord.gg/pcm8kWrdNt

### Gradle Groovy
```groovy
repositories {
    maven {
        url "https://repo.skyslycer.de/releases/"
    }
}

dependencies {
    compile "de.skyslycer.hmcwraps:api:1.0.0"
}
```

### Gradle Kotlin
```groovy
repositories {
    maven("https://repo.skyslycer.de/releases/")
}

dependencies {
    compile("de.skyslycer.hmcwraps:api:1.0.0")
}
```

### Maven
```xml
<repositories>
  <repository>
    <name>SkyRepo</name>
    <url>https://repo.skyslycer.de/releases/</url>
  </repository>  
</repositories>

<dependencies>
  <dependency>
    <groupId>de.skyslycer.hmcwraps</groupId>
    <artifactId>api</artifactId>
    <version>1.0.0</version>
  </dependency>
</dependencies>
```

## License
**HMCWraps** is licensed under the **GNU Affero General Public License v3.0**.

Permissions of this strongest copyleft license are conditioned on making available complete source code of licensed works and modifications, which include larger works using a licensed work, under the same license. 
Copyright and license notices must be preserved. Contributors provide an express grant of patent rights. When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.

_Read the entire [license](https://github.com/HibiscusMC/HMCWraps/blob/master/LICENSE) here._

**HMCWraps** is a paid resource and a license must be acquired on [SpigotMC.org](https://spigotmc.org) or rather [Polymart](https://polymart.org).
Nonetheless, nobody will prevent you from compiling the source and using that, as long as you don't redistribute it in any way without written consent (working, not working, compiled or not compiled, complete or partial).
Public forks are allowed, as long as they comply to the license, in order to create a pull request.

Even though compiling it yourself is an option, the source code is mainly there for contribution and acquiring a license 
supports HibiscusMC, Hibiscus Creative Studios and me, Skyslycer in creating awesome plugins for you.
Buying a license provides you with lifetime support and a pre-made resource pack too.
