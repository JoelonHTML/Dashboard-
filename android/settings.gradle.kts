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

rootProject.name = "DailyDashboard"

include(":app")

include(":feature:agenda")
include(":feature:herinneringen")
include(":feature:systeem")
include(":feature:fs25")

include(":core:network")
include(":core:database")
include(":core:designsystem")
include(":core:ui")
include(":core:datastore")
