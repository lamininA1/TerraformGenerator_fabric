dependencies {
    implementation(project(":common"))
    compileOnly("org.jetbrains:annotations:20.1.0")
    compileOnly("com.github.AvarionMC:yaml:1.1.7")
    implementation("net.fabricmc:fabric-loader:0.14.21")
    implementation("net.fabricmc.fabric-api:fabric-api:0.92.0+1.20.5")
    annotationProcessor("org.spongepowered:mixin:0.8.7")
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}