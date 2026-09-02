# Architecture

## Dependency direction

```text
                         ┌────────────────────┐
                         │        app         │
                         │ composition root   │
                         └─────────┬──────────┘
                                   │
                 ┌─────────────────┴─────────────────┐
                 ▼                                   ▼
          feature:api                        feature:impl
        public route contract                 Compose + MVI
                 ▲                                   │
                 └───────────────────────────────────┤
                                                     ▼
                                               domain
                                          use cases + ports
                                                     ▲
                                                     │
                                             data:impl
                                           repository adapters
                                                     │
                                                     ▼
                                              data:api
                                           source abstractions

All modules may depend downward on narrowly scoped core modules. No core or domain
module may depend on app, a feature implementation, or a data implementation.
```

`app` is allowed to know implementation modules because it is the composition root. Other features should depend on `feature:api`, never on `feature:impl`. Domain code owns repository ports; data code implements them. Framework types should not leak into domain contracts.

## MVI contract

```text
UI event -> Intent -> ViewModel/store -> reducer -> State -> Compose UI
                                  └-----> Effect -> one-shot collector
```

The `core:mvi` module defines only vocabulary and the store boundary. It deliberately does not provide a base ViewModel or generic reducer engine. Start with an explicit feature implementation; extract reusable mechanics only after at least two features prove the same need.

Recommended feature ownership:

- `ExchangeIntent`: every user or lifecycle input accepted by the feature.
- `ExchangeState`: a complete, immutable rendering model with sensible defaults.
- `ExchangeEffect`: non-replayable work such as navigation, messages, or external intents.
- `ExchangeViewModel`: the single intent entry point and state owner.
- Pure reducer functions: state transitions with no I/O.
- Use cases: orchestration and business rules, independent of Android.

## API/implementation rule

An `api` module contains the smallest stable surface that another module needs. An `impl` module contains volatile choices such as Retrofit, Room, Hilt bindings, ViewModels, and screen composition. Do not place concrete types in API signatures.

For this scaffold's bounded feature:

```text
feature:api     public navigation contract
feature:impl    UI, MVI contract, ViewModel, feature DI
domain          use cases, entities, repository ports
data:api        local/remote source ports when substitution is valuable
data:impl       source and repository adapters
```

Do not split a module merely to mirror folders. Add a module when it creates a useful dependency boundary, ownership boundary, build-isolation benefit, or replaceable implementation.

## State and concurrency rules

- Expose immutable `StateFlow` for durable UI state.
- Expose `Flow` for one-shot effects; do not encode events inside durable state.
- Accept intents through one public function.
- Keep mutable flows private to the owning ViewModel/store.
- Inject dispatchers through `AppDispatchers` where deterministic tests require control.
- Perform cancellation-sensitive work in structured scopes; avoid application-global coroutines.
- Model failures in domain terms before they reach the UI.

## Dependency injection

Hilt belongs at Android/framework boundaries. Prefer constructor injection. Use Hilt modules only to bind interfaces, configure third-party objects, or select qualified implementations. Keep domain and plain Kotlin modules free of Hilt annotations.

## Testing boundaries

- Reducers and use cases: fast JVM tests.
- ViewModels: JVM tests with fake ports, controlled dispatchers, and Turbine.
- Repository adapters: contract and integration tests against fake sources.
- Compose: semantics-based screen tests using state and callbacks directly.
- Navigation and Hilt wiring: a small number of instrumentation tests.

