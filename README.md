# HabilitesRandom

A Fabric mod for **Minecraft Java Edition 26.3**.

Prompt 1 foundation: modular core, externally extensible API, server-authoritative persistent player data, premium HUB, all initial screens, navigation, settings, keybind remapping foundation and an in-game HUD shell.

## Toolchain

| Component | Version |
|---|---|
| Minecraft | 26.3 |
| Java | 25 |
| Fabric Loader | 0.19.5 |
| Fabric API | 0.165.0+26.3 |
| Fabric Loom | 1.18-SNAPSHOT |
| Gradle | 9.7.1 |

Minecraft 26.x is unobfuscated, so this project uses the non-remapping `net.fabricmc.fabric-loom` plugin and Mojang names directly. It is not a Forge, NeoForge, Paper or hybrid project.

## Commands

```bash
./gradlew build
./gradlew test
# Requires a display. CI uses Xvfb + EGL for Minecraft 26.3's SDL window.
./gradlew runClientGameTest
```

The built mod is written to `build/libs/`. On a successful branch build, GitHub Actions also copies the verified remapped jar and its SHA-256 checksum into `dev-builds/`, making the development build reproducible from the repository itself.

## Architecture

- `api`: public contracts and addon entrypoint for future HabilitesRandom Plus projects.
- `core`: immutable runtime services and API versioning.
- `config`: client-side persisted settings.
- `data`: server-side saved player profiles and codecs.
- `network`: server-authoritative snapshot sync and action request plumbing.
- `player`: explicit server-authority validation boundary.
- `registry`: external content registration.
- `client`: entrypoint, key mappings, HUD and client cache.
- `screen`: the HUB and every initial page.
- `ui`: shared visual language, custom widgets, responsive layout and animation utilities.
- `util`: small validation/formatting helpers.

The Core never depends on a Plus. Plus mods can register characters, abilities, transformations, rarities, anime and arbitrary typed content through the public API.

## Current stage

The catalog, full abilities, randomization, shrines, Vampire Explorer and transformation logic intentionally remain unimplemented. Their extension points and authoritative data paths already exist so later prompts can add them without rebuilding the project.
