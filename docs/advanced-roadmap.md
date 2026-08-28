# Advanced Android and On-Device AI Roadmap

This document captures the more ambitious Android integrations from the original project specification. These are intentionally separated from Build 1 so the MVP can first prove the offline-first architecture without depending on device-specific AI availability or special-access permissions.

Platform policies, Google Play requirements, Gemini Nano availability, and Android API behavior should be revalidated against current official documentation before implementation or release.

## 1. Automated Proof of Testing via Usage Telemetry

### Goal

Reduce manual proof entry by using local Android usage telemetry to determine whether a tester has spent enough time in a target application to satisfy a Test Exchange campaign's own engagement threshold.

### Proposed Android Integration

- request user-granted usage access associated with `PACKAGE_USAGE_STATS`
- direct the user to the appropriate Android Settings screen when usage access is not available
- query `UsageStatsManager` for the target package
- calculate foreground-use duration for the relevant campaign window
- persist the resulting local proof record into Room
- enqueue synchronization through the same WorkManager pipeline used by manually submitted proof

### Important Product Boundary

Test Exchange may define a campaign threshold such as three minutes of foreground activity, but the product should not present that threshold as a Google Play requirement unless current Play policy explicitly says so.

Usage access is special access rather than a normal runtime permission, and users must deliberately grant it through system settings. The feature therefore needs a clear explanation and a manual fallback path.

### Data Flow

```text
UsageStatsManager
      |
local validation
      |
Room: proof record (SYNC_PENDING)
      |
WorkManager
      |
Backend
      |
Room reconciliation
```

This intentionally reuses the same persistence and reconciliation path as manual proof instead of creating a second network pipeline.

## 2. On-Device AI Arbitration with Gemini Nano

### Goal

Perform an initial quality check on submitted feedback locally before uploading it to the backend.

Potential local checks include:

- whether the report contains enough detail to be actionable
- whether the text appears coherent rather than empty/spam input
- whether the report appears related to the selected project
- whether locally supported image understanding can provide useful screenshot context

### Proposed Flow

1. user drafts a bug report and attaches proof
2. draft is persisted locally
3. device eligibility for the on-device model is checked
4. local AI evaluates the supported input
5. accepted reports proceed to the sync queue
6. weak reports receive immediate local feedback
7. backend remains authoritative for final arbitration and points

### Architectural Constraint

On-device AI must be an optimization, not a durability dependency.

If Gemini Nano/AICore is unavailable, unsupported for the requested modality, temporarily inaccessible, or fails, the report should still be able to enter the normal backend review path.

Suggested abstraction:

```text
interface LocalProofArbitrator {
    suspend fun evaluate(report: LocalReport): LocalArbitrationResult
}
```

The rest of the application depends on the interface rather than directly on a specific Gemini Nano API. That makes device fallback and future model changes easier.

## 3. Play Store Deep Linking

### Goal

Make joining a testing campaign feel native and minimize unnecessary browser steps.

### Proposed Behavior

When a tester taps **Test this App**:

- construct or validate the campaign's Google Play testing/opt-in URL
- launch it with `Intent.ACTION_VIEW`
- allow Android's intent resolution to route to the appropriate Play experience when supported
- provide a safe browser fallback if the Play Store cannot handle the URI

The application should not assume every Play testing URL can always bypass the browser on every device/configuration. Intent resolution and fallback behavior should be tested explicitly.

## 4. Native Share Intent Interception

This feature is part of Build 1 but becomes more powerful when combined with local AI.

### Advanced Flow

1. tester captures a screenshot in the target app
2. tester taps Share
3. Android sends the image URI to Test Exchange using `ACTION_SEND`
4. Test Exchange opens the Submit Proof flow
5. Room persists the draft and screenshot reference
6. local arbitration runs when available
7. accepted proof is queued for backend synchronization

### Edge Cases

- cold start from share intent
- multiple incoming shares
- URI permission lifetime
- unsupported MIME types
- large images
- image decoding memory pressure
- user abandons the draft after sharing

## 5. Authentication Storage Hardening

The original specification called for `EncryptedSharedPreferences` and hardware-level token storage. The implementation should keep the underlying requirement while avoiding an overly specific security claim.

### Requirement

- do not store session credentials as plaintext
- use Android platform security primitives appropriate to supported OS versions
- centralize credential access behind an interface
- inject credentials through OkHttp
- clear credentials on logout/session invalidation
- avoid exposing tokens to Compose UI state or logs

Potential implementations may use Android Keystore-backed encryption or the current recommended Android security APIs depending on platform/library support at implementation time.

## 6. Extended Offline-First Reconciliation

The advanced version can generalize synchronization beyond test reports.

Candidate synchronized entities:

- project participation state
- locally observed usage proof
- bug reports
- screenshots/attachments
- point-award results
- campaign metadata updates

If multiple entity types need independent synchronization, introduce a dedicated queue or outbox model rather than overloading each table with increasingly complex state.

Potential queue fields:

- operation ID
- entity type
- entity ID
- operation type
- attempt count
- next retry timestamp
- last error
- created timestamp
- idempotency key

## 7. Advanced Failure Scenarios

The state-of-the-art version should be exercised under conditions such as:

- device has no network for multiple days
- process dies after Room write but before WorkManager enqueue
- network becomes available while the device is in a constrained background state
- backend accepts a request but the client loses the response
- worker retries after server-side success
- session expires while background work is queued
- screenshot URI is no longer readable when the worker runs
- local AI is unavailable on the device
- local AI returns an error after the draft has already been persisted
- app updates while pending work exists

## 8. Target End State

The advanced client should preserve one consistent architectural rule across manual reports, usage telemetry, native share entry, and on-device AI:

> Capture durable local state first. Treat platform intelligence and network synchronization as recoverable processing stages around that state.

That keeps the application reliable while still allowing sophisticated Android integrations to be layered on top of the MVP.
