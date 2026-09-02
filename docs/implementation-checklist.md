# Implementation checklist

Every item below is deliberately left to the project owner.

## Domain first

- [ ] Define exchange terminology and immutable models in `core:model`.
- [ ] Define repository ports and use-case contracts in `domain`.
- [ ] Decide error, freshness, pagination, and market-session semantics.
- [ ] Write reducer and use-case tests before adding adapters.

## Data adapters

- [ ] Define only the replaceable source ports needed in `data:api`.
- [ ] Add serialization DTOs and Retrofit endpoints in `data:impl`.
- [ ] Add Room entities, DAOs, migrations, and mapping functions.
- [ ] Implement repository coordination, caching, and retry policy.
- [ ] Bind concrete adapters to domain ports in a Hilt module.

## Feature MVI

- [ ] Replace the empty feature intent/state/effect contracts with product decisions.
- [ ] Implement a ViewModel that owns mutable state and accepts all intents.
- [ ] Keep state reduction pure and isolate side effects.
- [ ] Connect the route to the ViewModel using lifecycle-aware collection.
- [ ] Replace the scaffold screen with stateless content composables.
- [ ] Add reducer, ViewModel, screenshot/semantics, and navigation tests.

## Production hardening

- [ ] Add build types and environment-specific endpoint configuration.
- [ ] Add secrets handling without committing credentials.
- [ ] Define observability, analytics, privacy, and redaction policy.
- [ ] Add baseline profiles and performance benchmarks where justified.
- [ ] Add CI for formatting, lint, unit tests, Android tests, and release builds.
- [ ] Add dependency verification and a deliberate update policy.

