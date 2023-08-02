rootProject.name = "OmegleChat"
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}

plugins {
}

rootDir.listFiles().filter { it.isDirectory && !it.isHidden }.forEach {
    print(it.name)
}

include(":app")
include(":api")