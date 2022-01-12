plugins {
    java
    signing
    `maven-publish`
    id("com.github.johnrengelman.shadow").version("7.1.0")
    id("io.papermc.paperweight.userdev").version("1.3.3")
}

group = "ru.ckateptb"
version = "1.0.12-SNAPSHOT"
var githubName = "AbilitySlots"
var githubOwner = "CKATEPTb"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    if (!isSnapshot()) {
        withJavadocJar()
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven {
        url = uri("https://maven.pkg.github.com/CKATEPTb/Tablecloth")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")

    paperDevBundle("1.17.1-R0.1-SNAPSHOT")

    compileOnly("ru.ckateptb:tablecloth:1.0.5-SNAPSHOT")
    compileOnly("dev.jorel.CommandAPI:commandapi-core:6.4.0")
}


tasks {
    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.${archiveExtension.getOrElse("jar")}")
    }
    build {
        dependsOn(reobfJar, shadowJar)
    }
    withType<Sign>().configureEach {
        onlyIf { !isSnapshot() }
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
                if (!isSnapshot()) {
                    artifact(tasks.javadoc)
                }
                artifact(tasks["sourcesJar"])
            }
            pom {
                name.set(project.name)
                url.set("https://github.com/${githubOwner}/${githubName}")
                licenses {
                    license {
                        name.set("The GNU Affero General Public License, Version 3.0")
                        url.set("https://www.gnu.org/licenses/agpl-3.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/${githubOwner}/${githubName}.git")
                    developerConnection.set("scm:git:ssh://git@github.com/${githubOwner}/${githubName}.git")
                    url.set("https://github.com/${githubOwner}/${githubName}")
                }
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/${githubOwner}/${githubName}/issues")
                }
            }
        }
        repositories {
            maven {
                name = githubName
                url = uri("https://maven.pkg.github.com/${githubOwner}/${githubName}")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

fun isSnapshot() = project.version.toString().endsWith("-SNAPSHOT")