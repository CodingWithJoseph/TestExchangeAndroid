# Test Exchange Android Architecture

## Objective

Deliver a native Android client for the existing Test Exchange platform that demonstrates resilient mobile architecture, deep Android integration, and production-style synchronization patterns.

The primary technical asset is the offline-first sync engine. Testing data should survive loss of connectivity, process death, app termination, and delayed backend availability without forcing the UI to block on a network request.

## Technical Stack

| Area | Technology |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose |
| Local persistence | Room |
| Background processing | WorkManager |
| Networking | Retrofit + OkHttp |
| Dependency injection | Hilt |
| Reactive state | Kotlin Flow |

## Build 1 Core Features

### Headless Authentication

The Android client communicates with the existing Test Exchange backend using authenticated requests. Session or JWT credentials are stored using an Android security-backed storage approach appropriate for the supported API level. An OkHttp interceptor is responsible for attaching the current token to outbound requests so individual Retrofit calls do not manually manage authentication headers.

Responsibilities:

- persist and retrieve the current authenticated session
- inject authorization headers through OkHttp
- centralize handling for expired or invalid sessions
- keep authentication concerns out of Compose screens

### Native Share Intent Interception

Users can capture a screenshot while testing another Android application and share that image directly to Test Exchange through the Android Share Sheet.

The application registers an `ACTION_SEND` intent filter for supported image MIME types. Incoming image URIs are normalized into the application's report-drafting flow so the user does not need to browse the file system manually.

### Offline-First Proof Logging

A report can be drafted and saved without connectivity. The UI writes the report to Room first, including local metadata, screenshot references, and synchronization state.

The local database is the client-side source of truth. Compose observes Room through `Flow`, so the report appears immediately even when no backend request has completed.

Suggested report synchronization states:

- `SYNC_PENDING`
- `SYNC_IN_PROGRESS`
- `SYNC_COMPLETE`
- `SYNC_FAILED`
- `PENDING_AI_REVIEW`
- `PROOF_ACCEPTED`
- `PROOF_REJECTED`

The exact state model can be refined during implementation to avoid overlapping transport state and business-review state.

### Backend AI Arbitration Handshake

Build 1 sends manually submitted proof to the existing backend. The backend AI arbitrator evaluates the screenshot and report description and returns the resulting review state and any awarded testing points.

The Android client is responsible for reliable delivery and reconciliation, not for performing the initial arbitration locally in Build 1.

## Offline-First Sync Engine

### 1. Local Capture

The tester submits a report and optional screenshot proof from the Compose UI or enters the flow through an incoming Android share intent.

### 2. Database Write

The UI does not make the upload request directly. The payload is written immediately to Room with a pending synchronization state.

This keeps interaction latency independent of network latency and ensures there is a durable record before synchronization begins.

### 3. UI Observation

Compose observes Room through Kotlin `Flow`. Once Room is updated, the UI reacts automatically and displays the locally saved report and its current state.

### 4. WorkManager Enqueue

A WorkManager request is enqueued with enough information to identify the pending record, typically the Room primary key rather than the entire payload.

Network constraints are attached so Android can defer execution until connectivity is available.

### 5. Condition Trigger

WorkManager monitors the declared execution constraints. When the required network condition is satisfied, Android can run the worker even when the user is no longer actively using the application, subject to normal platform background-execution rules.

### 6. Backend Execution

The worker reads the latest persisted payload from Room and sends it through the repository/network layer using Retrofit and OkHttp.

The backend stores the proof and performs AI arbitration.

### 7. Reconciliation

On success, the worker updates Room with the server result, synchronization status, review status, and any awarded points.

Because Compose is observing Room, the UI updates from the database rather than from an ad hoc callback from the network request.

On a retryable failure, the local record remains durable and WorkManager can retry according to the configured policy.

## Intended Layering

A practical implementation should keep responsibilities separated roughly as follows:

```text
Compose UI
    |
ViewModel
    |
Repository
   / \
Room  Retrofit/OkHttp
  ^        |
  |        |
  +-- WorkManager Worker
```

### UI Layer

- Compose screens
- ViewModels
- immutable UI state
- collection of Room-backed flows

### Data Layer

- Room entities and DAOs
- API DTOs
- Retrofit interfaces
- repository implementations
- mapping between local and remote models

### Background Layer

- WorkManager workers
- sync scheduling
- retry/backoff behavior
- reconciliation logic

### Platform Integration Layer

- incoming `ACTION_SEND` handling
- Play Store/deep-link intents when introduced
- permission/settings navigation for advanced telemetry features

## Initial Domain Models

### Project

Represents an Android application that needs testers.

Potential fields:

- project ID
- developer/owner ID
- application name
- package name
- Play testing URL
- campaign state
- testing requirements

### Test Report

Represents tester-submitted proof and feedback.

Potential fields:

- local ID
- remote ID
- project ID
- tester ID
- description
- screenshot URI/reference
- created timestamp
- synchronization state
- review state
- awarded points
- last synchronization error

### Sync Queue State

The first implementation may keep synchronization fields directly on each report rather than create a generic queue table. A dedicated queue abstraction should only be introduced if multiple independently synchronized entity types make it useful.

## Edge Cases to Exercise

The MVP should explicitly test the failure modes that make the architecture interview-worthy:

- submit while offline
- toggle Airplane mode during synchronization
- terminate the app after the local database write but before upload
- terminate the process while a worker is running
- retry a transient server failure
- avoid duplicate submissions after worker retries
- handle a permanent validation failure without retrying forever
- restore pending UI state after process recreation
- receive a shared screenshot while the app is cold-started
- handle a content URI whose permission lifetime is limited
- reconcile a server response after the user navigates away

## Architectural Principle

The key rule for Build 1 is:

> Persist first, synchronize second, render from local state.

That rule keeps the UI responsive and makes failures recoverable instead of transient network events controlling application state.
