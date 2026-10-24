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

The built mod is written to `build/libs/`. On a successful branch build, GitHub Actions also copies the verified remapped jar and its SHA-256 checksum into `dev-builds/`, making the development build reproducible from the repository itself. The successful headless client smoke test commits real captures of every initial screen to `qa/screenshots/`.

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

## Character and ability architecture (Prompt 2)

The Core now registers a 91-character launch catalog spanning the ten requested universes. Strength (`STRONG`/`WEAK`), rarity, and independent positive draw weight are separate fields. Every character graph is startup-validated and contains exactly four registered abilities and one registered powerful form.

Public extension points remain under `com.habilitesrandom.api`. A companion mod implements `HabilitesRandomAddon` and receives a `HabilitesRandomRegistrar`; it should register anime/rarity and ability/transformation definitions before its character definitions. Definitions are immutable and duplicate IDs are rejected. `CharacterRegistry`, `AbilityRegistry`, and `TransformationRegistry` provide typed lookup/query APIs. The Core never depends on a Plus mod.

Abilities describe composable behavior types, geometry, damage, range, duration, status effects, safe sound/particle identifiers, knockback, projectile limits, and bounded block interaction. `CombatPolicy` applies server safety caps; block destruction is disabled in its safe default. `CooldownManager` writes independent epoch deadlines into persistent server profiles. The complete key-driven executor and Randomizer selection remain intentionally scheduled for Prompt 3.

To add a character:
1. Register four unique `AbilityDefinition` values with positive cooldowns.
2. Register one `TransformationDefinition`.
3. Register a `CharacterDefinition` referencing those IDs, a registered anime and rarity, a category, and a positive independent weight.
4. Keep all execution requests server-authoritative; never mutate gameplay from client code.

Run `./gradlew build` for unit/integrity tests. Run `./gradlew runClientGameTest` in a graphical/Xvfb environment for the real-client navigation smoke test.

## Randomizer, controls, and server policy (Prompt 3)

Global Fabric mappings are Z/X/C/V for ability slots 1–4 and left Alt for the powerful form. They are visible and remappable in Minecraft controls; the HUB controls page edits those same mappings. The client sends only an action and slot. `PlayerAuthorityGate` resolves the persisted character and registered loadout, checks life/cooldown/configuration, and the server executes bounded combat and starts the persisted cooldown.

The first draw is explicitly requested from the Randomizer screen. `RandomizerService` reads every enabled `CharacterRegistry` entry—including entries supplied by Plus addons—normalizes independent weights, applies a bounded category-only anti-luck multiplier, assigns the complete loadout, saves it, and only then sends the reveal payload. A second normal draw is rejected. Configure gameplay in `config/habilitesrandom/server.json`; options include enablement, allow/block filters, weight overrides, anti-luck, announcements, ability execution and safety caps. Server configuration is authoritative.

## Reroll and Sanctuary state (Prompt 4)

The first server-approved draw grants exactly one persisted free reroll. `RerollService` reuses `RandomizerService`, therefore weights, anti-luck, filters, registries and Plus content remain identical. A completed replacement resets the old character cooldowns through `CharacterAssignmentService`. The persistent progression is one-way: free reroll, sanctuary required, final reroll available, exhausted.

The sanctuary transaction policy defaults to 15 emeralds and 50% of current health while preserving at least one health point. Eligibility, inventory and health are validated before commit. Sanctuary map coordinates and the individual altar reward are stored in the existing `PlayerProfile`; the shared location never acts as a global reward lock. Server options live in `config/habilitesrandom/server.json`.

## Powerful forms and Powerful Fruit (Prompt 5)

The global Alt mapping requests the registered transformation from the server. Daily availability is calculated lazily from the Overworld day (`dayTime / 24000`), so natural dawn, sleeping and time commands all reset charges without a per-player every-tick scan. Profiles persist the last use day, uses on that day and active expiry. Active forms apply the registered damage multiplier to the existing ability executor; expiration and death are reconciled once per second, while reroll clears the prior active form without refunding its daily charge.

`habilitesrandom:powerful_fruit` is a registered item with an original texture, model, translations and data-pack recipe. One consumption permanently changes the profile from one to two daily form charges; repeat consumption is rejected and cannot stack. It remains obtainable with `/give` when recipes are disabled by a server pack.
