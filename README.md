# HabilitesRandom

A server-authoritative **Fabric mod for Minecraft Java Edition 26.3**. Version `1.0.0+26.3`, public Plus API `1.0`.

## Features

- Responsive HUB, Randomizer, Minha Habilidade, Reroll, Sanctuary, controls/settings, result screens, and HUD in English and Brazilian Portuguese.
- Persistent server-owned profiles, weighted draws, bounded anti-luck, one free and one Sanctuary-gated final reroll.
- 92 registered characters, 368 abilities, and 92 powerful forms across ten franchises.
- Universal remappable Z/X/C/V abilities and left-Alt form activation. Clients send intent only; assignment, RNG, cooldowns, combat, rewards, forms, and saves are decided server-side.
- Overworld-day form charges, duration/death/relog cleanup, and permanent Powerful Fruit entitlement.
- Public immutable Plus API with stable IDs, graph validation, executable server abilities, form hooks, read-only player views, and observation events.
- Independent Test Plus project proving registration of one character, four abilities, one form, and a custom movement executor.

## Requirements

| Component | Version |
|---|---|
| Minecraft | 26.3 |
| Java | 25 |
| Fabric Loader | 0.19.5 |
| Fabric API | 0.165.0+26.3 |
| Fabric Loom | 1.18-SNAPSHOT |
| Gradle | 9.7.1 |

This is not a Forge, NeoForge, Paper, or hybrid project.

## Build and test

```bash
./gradlew clean build :example-plus:build
# Requires a display; CI uses Xvfb and EGL.
./gradlew runClientGameTest
```

Core output is under `build/libs/`; the independent addon is under `examples/HabilitesRandom-Test-Plus/build/libs/`. GitHub Actions publishes verified binaries and SHA-256 files to `dev-builds/` and the complete user bundle to `release/`.

## Architecture

Core owns one catalog, Randomizer, Reroll, PlayerData path, network authority boundary, HUD, controls, cooldown executor, and form runtime. External addons are loaded through Fabric's `habilitesrandom:addon` entrypoint. Core never depends on or imports Plus code; the example depends on Core's public `com.habilitesrandom.api` packages.

See [API reference](docs/API.md), [Plus creation guide](docs/CREATING_A_PLUS.md), [example project](examples/HabilitesRandom-Test-Plus), and [changelog](CHANGELOG.md).

## Configuration and safety

Gameplay configuration is server-owned at `config/habilitesrandom/server.json`: content filters, weight overrides, anti-luck, announcements, execution, Sanctuary policy, and combat/world-interaction limits. Declarative abilities are clamped by Core policy. Custom Plus executors run on the logical server and must cooperate with protection/claim mods before changing the world.

Removing an installed Plus does not erase the player's profile. Its stable character/loadout IDs remain persisted; unavailable actions fail safely until the Plus is restored or an authorized reroll assigns available content.

## Validation status and limitations

CI runs compilation, unit/integrity tests, separate Plus compilation, and a real Fabric client navigation smoke test. It does **not** constitute manual multiplayer, dedicated-server, restart persistence, every-character combat, Sanctuary world-generation, or performance/load validation. Several built-in abilities/forms intentionally use shared declarative mechanics rather than bespoke franchise-specific implementations. Consult the release notes and workflow results rather than assuming those gameplay scenarios were manually certified.
