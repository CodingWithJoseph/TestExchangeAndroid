# Test Exchange

An intentionally implementation-free Android project scaffold for a modular Model-View-Intent application.

The project includes build configuration, dependency boundaries, navigation wiring, a minimal launchable Compose shell, and explicit extension points. It intentionally does **not** include reducers, business rules, repositories, network calls, persistence, feature ViewModels, or production UI.

## Open the project

Open this directory in the current stable Android Studio and let Gradle sync. The scaffold targets API 37, has a minimum SDK of 24, and requires JDK 17 or newer.

```text
TestExchange/
├── app/                       # Application composition root only
├── core/
│   ├── common/                # Platform-neutral shared contracts
│   ├── model/                 # Shared domain models (empty by design)
│   ├── mvi/                   # MVI contracts; no store implementation
│   └── designsystem/          # Compose theme and reusable UI foundation
├── domain/                    # Use cases and repository ports (TODO)
├── data/
│   ├── api/                   # Data-source abstractions (TODO)
│   └── impl/                  # Retrofit/Room/repository adapters (TODO)
├── feature/
│   ├── api/                   # Public route contract
│   └── impl/                  # Compose/MVI feature implementation seam
├── testing/                   # Shared fakes, rules, and fixtures (TODO)
└── docs/                      # Architecture and implementation guide
```

Start with [docs/architecture.md](docs/architecture.md), then work through [docs/implementation-checklist.md](docs/implementation-checklist.md). Search for `TODO(owner)` to find every intentionally unfinished seam.

## Included foundations

- Kotlin with AGP 9 built-in Kotlin support
- Jetpack Compose and Material 3 through the Compose BOM
- Coroutines, `Flow`, `StateFlow`, and one-shot effect contracts
- Hilt with KSP at Android composition boundaries
- Typed Navigation Compose route contracts
- API/implementation module separation for features and data
- Retrofit, OkHttp, kotlinx.serialization, Room, and DataStore dependency slots
- Unit, coroutine, Flow, Compose UI, and instrumentation test dependency slots
- Central version catalog and configuration-cache-friendly Gradle settings

Dependency versions are centralized in `gradle/libs.versions.toml`; update them there only.

This environment could not fetch the Gradle distribution, so wrapper scripts are not generated here. Android Studio can sync the project directly; on a machine with Gradle available, generate the wrapper with `gradle wrapper --gradle-version 9.4.1 --distribution-type bin`.
