# Build 1 Execution Plan

## Goal

Build the Android MVP in four phases, with each phase producing a testable slice of the final architecture. The priority is to prove the data flow and failure behavior before spending time on visual polish.

## Phase 1: Data and Network Layer

### Objectives

Establish the local source of truth, backend contract, and dependency graph.

### Tasks

- create the Android application structure in Kotlin with Jetpack Compose
- configure Hilt
- define Room database, entities, DAOs, and migrations strategy
- model `Project` and `TestReport`
- define synchronization and review-state enums
- map the existing Test Exchange backend endpoints into Retrofit interfaces
- configure OkHttp
- add the authentication interceptor
- define local/remote model mapping
- create repository interfaces and implementations
- expose Room-backed data as Kotlin `Flow`

### Exit Criteria

- a report can be created locally with the network disabled
- Room persists the report after process recreation
- a ViewModel can observe the report list as `Flow`
- authenticated Retrofit calls can be made independently of the UI

## Phase 2: Offline-First Sync Engine

### Objectives

Guarantee that locally captured proof can be reconciled with the backend after connectivity returns.

### Tasks

- create the WorkManager worker for report synchronization
- enqueue work using the local report primary key
- add appropriate network constraints
- read the latest record from Room inside the worker
- upload through the repository/Retrofit layer
- persist server IDs and response data
- update synchronization state on success
- distinguish retryable failures from permanent failures
- configure retry/backoff behavior
- design idempotency or duplicate-submission protection with the backend

### Edge-Case Tests

- submit while offline
- enable connectivity after submission
- toggle Airplane mode mid-upload
- kill the app before the worker executes
- terminate the process during worker execution
- return a transient 5xx response
- return a permanent 4xx validation response
- execute the same worker more than once
- restore pending state after device/app restart

### Exit Criteria

- a locally persisted pending report eventually uploads after connectivity returns
- retryable errors do not lose the report
- successful reconciliation updates Room
- Compose can reflect the final state solely by observing Room

## Phase 3: Jetpack Compose and Native Intents

### Objectives

Build the user-facing testing workflow and connect native Android entry points.

### Initial Screens

#### Dashboard

- active testing campaigns
- pending reports
- synchronized reports
- awarded points/status summary

#### Project Details

- project/app information
- package name
- Play testing/opt-in action
- testing instructions
- submitted proof history
- current participation state

#### Submit Proof

- bug/feedback description
- screenshot attachment
- local save status
- synchronization status
- AI review result when available

### Native Share Intent

- register an `ACTION_SEND` intent filter for image MIME types
- accept a shared screenshot URI
- preserve URI access as required by Android content-provider rules
- route the image into the Submit Proof flow
- support cold-start and already-running app cases

### UI State Requirements

The UI should explicitly represent states such as:

- saved locally
- waiting for network
- syncing
- pending AI review
- points awarded
- proof rejected
- synchronization failed with retry available

### Exit Criteria

- users can create proof entirely through Compose
- users can share a screenshot from another app into Test Exchange
- UI state survives recreation because it is derived from persisted data

## Phase 4: Backend AI Handshake and Polish

### Objectives

Complete the server arbitration loop and harden the MVP for demonstration.

### Tasks

- finalize proof payload delivery to the backend AI arbitrator
- reconcile AI review results into Room
- surface awarded points and rejection reasons
- add loading, empty, error, and recovery states
- improve accessibility and Compose state handling
- verify navigation after share-intent entry
- test authentication expiration and re-login behavior
- add unit tests for repositories, workers, state transitions, and ViewModels
- add targeted instrumentation tests for Room/WorkManager integration where valuable

### Exit Criteria

A user can:

1. authenticate
2. select a project
3. create or share screenshot proof
4. save the report while offline
5. close the app
6. regain connectivity
7. have WorkManager deliver the pending report
8. receive the backend AI decision
9. see the resulting status and points through Room-backed UI state

## Suggested Implementation Order

Within each phase, prefer vertical behavior over creating every abstraction up front. A strong first end-to-end slice is:

1. one `TestReport` Room entity
2. one DAO
3. one fake/simple backend endpoint contract
4. one repository
5. one WorkManager worker
6. one ViewModel
7. one Compose report list
8. one offline-to-online synchronization test

Once that path works, expand into projects, screenshots, authentication edge cases, and AI review states.

## Interview-Focused Validation

The finished MVP should make it easy to explain these engineering decisions:

- why Room is the local source of truth
- why the UI does not call Retrofit directly
- how `Flow` keeps Compose reactive
- what WorkManager guarantees and what it does not guarantee
- how retries can cause duplicate requests and how idempotency addresses that
- how app/process death affects queued work
- how Android content URIs differ from file paths
- why transport synchronization state differs from backend business-review state
- why authentication belongs in a shared networking layer
- how the architecture can evolve without rewriting the UI
