import org.gradle.api.initialization.resolve.RepositoriesMode

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TestExchange"

include(":app")
include(":core:common")
include(":core:model")
include(":core:mvi")
include(":core:designsystem")
include(":domain")
include(":data:api")
include(":data:impl")
include(":feature:api")
include(":feature:impl")
include(":testing")
