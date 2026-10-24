# Creating a HabilitesRandom Plus

1. Create a Fabric 26.3 project using Java 25, Loader 0.19.5, and Fabric API.
2. Add the released Core JAR as a compile dependency or Maven dependency supplied by your distribution.
3. Implement `HabilitesRandomAddon`; import only `com.habilitesrandom.api` contracts.
4. Register structured anime metadata.
5. Build four `AbilityDefinition` values. Register an `AbilityExecutor` only when declarative Core behavior is insufficient.
6. Build one `TransformationDefinition` and optional `TransformationHooks`.
7. Build and register the `CharacterDefinition` last.
8. Declare the entrypoint and mandatory dependency:

```json
{
  "entrypoints": {"habilitesrandom:addon": ["your.package.YourAddon"]},
  "depends": {"habilitesrandom": ">=1.0.0", "minecraft": "~26.3", "fabric-api": "*"}
}
```

9. Run your build and place both JARs in `mods/`.
10. Start a server and matching clients. Confirm registration in the Core log.

See `examples/HabilitesRandom-Test-Plus` for a compilable external project with one character, four abilities, one custom executor, and one powerful form. Its output is a separate JAR.
