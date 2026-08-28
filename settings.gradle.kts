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
include(":domain:exchange")
include(":data:exchange:api")
include(":data:exchange:impl")
include(":feature:exchange:api")
include(":feature:exchange:impl")
include(":testing")
