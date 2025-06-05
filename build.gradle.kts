import java.io.ByteArrayOutputStream
import java.net.URL
import java.nio.channels.Channels

plugins {
    java
}

subprojects {
    apply<JavaPlugin>()

    group = "org.terraform"

    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://repo.spongepowered.org/maven")
        maven("https://jitpack.io")
    }
//    Handle this inside each implementation as different minecraft versions support a different max jvm version
//    java {
//        sourceCompatibility = JavaVersion.VERSION_16
//        targetCompatibility = JavaVersion.VERSION_16
//    }
}

fun gitClone(name: String) {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = mutableListOf("git", "clone", name)
        standardOutput = stdout
    }
}