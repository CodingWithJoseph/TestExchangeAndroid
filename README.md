# Test Exchange Android

Native Android client for the Test Exchange platform, designed to help Android developers coordinate closed testing campaigns and collect resilient proof-of-testing data.

## Objective

Build a portfolio-quality Android application around modern production patterns rather than a thin web wrapper. The client is centered on an offline-first architecture, native Android integrations, background synchronization, secure backend communication, and reactive Jetpack Compose UI.

## Core Stack

- Kotlin
- Jetpack Compose
- Room
- WorkManager
- Retrofit
- OkHttp
- Hilt

## Build 1 Focus

The first build prioritizes the architecture that is most valuable both to the product and to Android engineering interviews:

- offline-first report capture with Room as the local source of truth
- WorkManager-based background reconciliation
- Retrofit/OkHttp communication with the existing Test Exchange backend
- authenticated API requests through an OkHttp interceptor
- native Android Share Sheet support for screenshot proof
- Jetpack Compose screens driven by Kotlin Flow
- backend AI arbitration for submitted proof

The UI does not directly own network synchronization. User actions are persisted locally first, reflected immediately in the UI, and then reconciled with the backend through background work.

## Documentation

- [Architecture and data flow](docs/architecture.md)
- [Build 1 execution plan](docs/build-plan.md)
- [Advanced Android and on-device AI roadmap](docs/advanced-roadmap.md)

## Product Scope

Test Exchange is intended to support developers who need real testers for Google Play closed-testing workflows. Policy-specific requirements should be validated against the current Google Play Console rules before release because Google may change tester counts, duration requirements, or eligibility rules over time.
