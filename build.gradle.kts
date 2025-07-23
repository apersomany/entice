import xyz.jpenilla.runpaper.task.RunServer

plugins {
    id("java")
    id("fabric-loom") version "1.10-SNAPSHOT"
    id("xyz.jpenilla.run-paper") version "2.3.1" apply false
}

base {
    archivesName = project.name
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}

repositories {
    maven("https://api.modrinth.com/maven")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://maven.ladysnake.org/releases") {
        content {
            includeGroup("io.github.ladysnake")
            includeGroup("org.ladysnake")
            includeGroupByRegex("'dev\\.onyxstudios.*'")
        }
    }

}

dependencies {
    minecraft("com.mojang:minecraft:1.21.1")
    // Mojang Mappings
    mappings(loom.officialMojangMappings())
    // Fabric
    modImplementation("net.fabricmc:fabric-loader:0.16.10")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.115.4+1.21.1")
    modImplementation(include("org.ladysnake:satin:2.0.0")!!)
    // Common Dependencies
    modImplementation(include("org.apache.fury:fury-core:0.10.0")!!)
    // Fabric Dependencies
    modCompileOnly("maven.modrinth:sodium:c3YkZvne")
    modCompileOnly("maven.modrinth:iris:zsoi0dso")
    // Bukkit
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    // Bukkit Dependencies
    implementation("io.lumine:Mythic-Dist:5.8.2")
    implementation("org.ow2.asm:asm:9.2")
}

val runServer = tasks.register<RunServer>("runPaperServer") {
    dependsOn(tasks.jar)
    group = "paper"
    minecraftVersion("1.21.1")
    pluginJars(tasks.jar.get().archiveFile::get)
}