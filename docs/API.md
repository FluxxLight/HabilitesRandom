# HabilitesRandom Public API 1.0

Everything under `com.habilitesrandom.api` is public API. Packages such as `data`, `player`, `network`, `content`, and `core` are implementation details.

## Loading an addon

Implement `HabilitesRandomAddon` and expose it through the Fabric entrypoint `habilitesrandom:addon`. `requiredApiVersion()` returns the supported API major (currently `1`). The Core catches an addon's registration failure, logs its addon ID, and continues loading other addons.

Registration order is important: anime and optional custom rarity, then four abilities, one transformation, optional executors/hooks, and finally the character. A character graph with missing references is rejected.

```java
public final class MyPlus implements HabilitesRandomAddon {
  public String addonId() { return "my_plus"; }
  public int requiredApiVersion() { return 1; }
  public void register(HabilitesRandomRegistrar r) {
    Identifier anime = id("series");
    r.registerAnime(new AnimeDefinition(anime, "Series", "anime.my_plus.series"));

    AbilityDefinition first = AbilityDefinition.builder(id("strike"))
        .displayName("Strike").description("Server attack")
        .cooldownTicks(160).damage(8).range(5)
        .types(AbilityType.MELEE)
        .presentation("minecraft:crit", "minecraft:entity.player.attack.strong")
        .build();
    // Register first plus three more definitions.
    r.registerAbility(first);

    TransformationDefinition form = TransformationDefinition.builder(id("awakened"))
        .displayName("Awakened").durationTicks(600)
        .modifiers(1.5, 1.2, 1.2)
        .presentation("minecraft:flash", "minecraft:item.totem.use").build();
    r.registerTransformation(form);

    r.registerAbilityExecutor(first.id(), context -> {
      // Runs on the logical server after Core validates player, slot and cooldown.
      return AbilityExecutionResult.success(1, 8);
    });

    r.registerCharacter(CharacterDefinition.builder(id("hero"))
        .displayName("Hero").anime(anime)
        .rarity(Identifier.fromNamespaceAndPath("habilitesrandom", "epic"))
        .category(StrengthCategory.STRONG).weight(20)
        .abilities(id("strike"), id("second"), id("third"), id("fourth"))
        .powerfulForm(form.id()).build());
  }
}
```

## Execution

Abilities without a custom executor use Core's bounded declarative executor. `AbilityExecutor` supports arbitrary safe server behavior through its trusted `ServerPlayer` context. Plus code remains responsible for respecting claims/protection mods when performing custom world edits. Returning a rejected result prevents cooldown consumption.

`TransformationHooks` observes activation/end while Core retains ownership of ALT, daily charges, Powerful Fruit, duration, persistence, and synchronization.

## Player API and events

`CoreServices.api().players().view(player)` returns an immutable `PlayerStateView`. It cannot mutate SavedData. Server-only events are available through `HabilitesRandomEvents`: character assignment/reroll, ability use, form activation, and form end. Listener exceptions are isolated.

## Automatic integration

The single catalog drives Randomizer, Reroll, Minha Habilidade, HUD, Z/X/C/V/ALT controls, cooldowns, daily forms, and persistence. There are no per-Plus randomizers or key mappings. Stable IDs must use the addon's namespace.

If a Plus is removed while its character is saved, the raw profile is preserved. The UI safely shows the stable ID/fallback data, and gameplay actions are denied until that content is installed again or an authorized reroll changes the character.

## Persistence compatibility

Profiles carry schema version `2`. At server startup, older additive schemas are upgraded in place and one aggregate migration message is logged; identity, reroll/Sanctuary progression, loadout, cooldowns, form state, and fruit entitlement are retained. Unknown stable IDs are not discarded, which makes temporary Plus removal reversible.
