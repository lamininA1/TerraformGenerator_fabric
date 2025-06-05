import groovy.json.JsonSlurper

plugins {
    id("com.gradleup.shadow").version("9.0.0-beta2")
}

buildscript {
    dependencies {
        classpath("org.yaml:snakeyaml:2.0")
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":implementation:v1_18_R2"))
    implementation(project(":implementation:v1_19_R3"))
    implementation(project(":implementation:v1_20_R1"))
    implementation(project(":implementation:v1_20_R2"))
    implementation(project(":implementation:v1_20_R3"))
    implementation(project(":implementation:v1_20_R4"))
    implementation(project(":implementation:v1_21_R1"))
    implementation(project(":implementation:v1_21_R2"))
    implementation(project(":implementation:v1_21_R3"))
    implementation(project(":implementation:v1_21_R4"))
    implementation("com.github.AvarionMC:yaml:1.1.7")
}

tasks.shadowJar {
    doFirst {
        val jsonFile = file("${rootProject.projectDir}/common/src/main/resources/fabric.mod.json")
        val json = JsonSlurper().parse(jsonFile) as Map<*, *>

        // Set the archive name and version based on the fabric.mod.json file
        archiveBaseName.set(json["name"].toString())
        archiveVersion.set(json["version"].toString())
        archiveClassifier.set("") // Don't add the '-all' postfix.
    }

    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}

tasks.register<Copy>("deploy") {
    dependsOn(tasks.named("shadowJar"))

    from(layout.buildDirectory.dir("libs"))
    include("*.jar")
    into(rootProject.projectDir)

    doNotTrackState("Disable state tracking due to file access issues")
}