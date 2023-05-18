pluginManagement {
    repositories {
        maven("https://maven.blamejared.com")
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        maven("https://repo.spongepowered.org/repository/maven-public/") {
            name = "Sponge Snapshots"
        }
    }
}
rootProject.name = "searchables"
include("common")
include("fabric")
include("forge")
