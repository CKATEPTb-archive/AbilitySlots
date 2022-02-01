plugins {
    java
    signing
    `maven-publish`
    id("com.github.johnrengelman.shadow").version("7.1.0")
    id("io.papermc.paperweight.userdev").version("1.3.3")
}

group = "ru.ckateptb"
version = "1.2.3"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/") // WorldGuard
    maven("https://ci.ender.zone/plugin/repository/everything/") // LWC
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")

//    paperDevBundle("1.18.1-R0.1-SNAPSHOT")
    paperDevBundle("1.17.1-R0.1-SNAPSHOT")

    compileOnly("ml.tablecloth:Tablecloth:ab65aa449b")
    compileOnly("dev.jorel.CommandAPI:commandapi-core:6.4.0")

    // PROTECTION PLUGINS
    compileOnly("com.github.TechFortress", "GriefPrevention", "16.17.1")
    compileOnly("com.github.TownyAdvanced", "Towny", "0.97.5.0")
    compileOnly("com.griefcraft.lwc", "LWCX", "2.2.6")
    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.0.0") {
        exclude(module = "bukkit")
    }
}


tasks {
    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.${archiveExtension.getOrElse("jar")}")
    }
    build {
        dependsOn(reobfJar, shadowJar)
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    named<Copy>("processResources") {
        filesMatching("plugin.yml") {
            expand("projectVersion" to project.version)
        }
        from("LICENSE") {
            rename { "${project.name.toUpperCase()}_${it}" }
        }
    }
}

publishing {
    publications {
        publications.create<MavenPublication>("maven") {
            artifacts {
                artifact(tasks.shadowJar) {
                    classifier = ""
                }
                artifact(tasks["sourcesJar"])
            }
        }
    }
}