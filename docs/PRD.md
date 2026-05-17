# Thaumcraft 4.2.3.5 -> Minecraft Forge 1.12.2 Port PRD

## 1. Purpose

Port Thaumcraft 4.2.3.5 from Minecraft 1.7.10 to Minecraft Forge 1.12.2 while preserving gameplay behavior, addon API compatibility, registry identity, save-data semantics, research/crafting progression, worldgen behavior, and client presentation as closely as Forge 1.12.2 allows.

This is not a modernization rewrite. The goal is original Thaumcraft 4.2 behavior on a Forge 1.12.2 technical surface.

## 2. Non-goals

- Do not port to another Minecraft version.
- Do not move to Fabric or modern Forge.
- Do not redesign Thaumcraft mechanics.
- Do not change mod identity or registry names for convenience.
- Do not replace existing architecture with a new architecture unless required by Forge 1.12.2 API changes.
- Do not add JEI, config GUI, localization breadth, or optional compatibility before required gameplay/client/content parity is stable.
- Do not claim full parity only because `compileJava` passes.

## 3. Project facts

The repository is a Java Minecraft Forge mod.

The build uses:

- `apiJar` for `thaumcraft/api/**`.
- `devJar` for development/debugging.

The Docker development image exists to provide a Java 8 environment with CFR, ripgrep, Python, build tools, and Forge client runtime libraries.

## 4. Source layout

Primary implementation:

- `src/main/java/thaumcraft/api/**`
- `src/main/java/thaumcraft/common/**`
- `src/main/java/thaumcraft/client/**`
- `src/main/java/thaumcraft/codechicken/**`
- `src/main/java/thaumcraft/truetyper/**` (declares package `truetyper`)
- `src/main/resources/**`

Reference material:

- `thaumcraft_src/**`
- `Thaumcraft-1.7.10-4.2.3.5.jar`

Documentation:

- `AGENTS.md`
- `docs/PRD.md`
- `docs/Stage1.md` through `docs/Stage7.md` — phase-specific gap analyses and closure plans.
- `docs/Stage8-a.md` through `docs/Stage8-e.md` — split Stage 8 client GUI/render/FX gap analyses and closure plans.
- `docs/Stage9-a.md` through `docs/Stage9-e.md` — split Stage 9 content/recipe/research gap analyses and closure plans.

## 5. Public contracts that must remain stable

The following are compatibility boundaries:

- `thaumcraft.api.*` public class names, method signatures, package names, and expected addon API behavior.
- Mod id and metadata.
- Registry names for blocks, items, entities, potions, enchantments, dimensions, biomes, sounds, recipes, and GUI ids.
- NBT keys for items, wands, foci, baubles, tiles, entities, research, warp, aura, vis, dimensions, and maze/world data.
- Config keys and default meanings.
- Network packet registration and payload compatibility where already established.
- Resource paths under `assets/thaumcraft/**`.
- Baubles integration semantics.
- Fresh-world runtime identity for registry, config, NBT, network, resource, and API data. Old 1.7.10/WIP saves are not a target.

## 6. Architecture

### 6.1 Public API layer

`thaumcraft.api.*` is the addon-facing API layer.

Responsibilities:

- Aspects and aspect lists.
- Wand/focus API types.
- Research API types.
- Crafting API types.
- Node and vis API types.
- Interfaces used by addon mods.
- API-only fallback/internal handler types.

Rules:

- Keep API signatures stable.
- Keep implementation-heavy behavior out of API unless original API requires it.
- `apiJar` must continue to include `thaumcraft/api/**`.

### 6.2 Common/server layer

`thaumcraft.common.*` owns gameplay.

Responsibilities:

- Forge lifecycle integration.
- Registration of blocks/items/entities/potions/enchantments/recipes.
- Config.
- Network packets and server handlers.
- Blocks and tile entities.
- Items, tools, armor, baubles, relics, wands, and foci.
- Research state, warp, scans, aspects, recipes, crafting systems.
- Entity AI, projectile behavior, boss behavior, golems.
- World generation and Outer Lands.
- Persistent NBT/world data/capabilities.

Rules:

- Server decisions must not live in client code.
- Side checks must be explicit for world/entity mutations.
- Preserve existing NBT/config/registry identity.
- Prefer original 1.7.10 behavior, adapted to Forge 1.12.2.

### 6.3 Client layer

`thaumcraft.client.*` owns client-only functionality.

Responsibilities:

- GUI screens.
- Client event handlers.
- Key bindings.
- Item/block/entity renderer registration.
- Tile entity special renderers.
- Entity renderers.
- Model classes.
- Particles, beams, bolts, shader/post-processing effects.
- Client-only sound/visual feedback.

Rules:

- Client-only classes must not be referenced from common/server-only code paths.
- Use Forge 1.12.2 rendering APIs.
- Keep original visual behavior where practical.
- Prefer existing bundled helpers only when they are already part of the porting strategy.

### 6.4 Resource layer

`src/main/resources/**` owns:

- `mcmod.info`.
- Blockstates.
- Item/block models.
- Textures.
- GUI textures.
- Sounds and `sounds.json`.
- Recipes.
- Language files.
- Shader resources.

Rules:

- Do not rename existing resource paths without a compatibility reason.
- Add missing resources required by implemented client/content parity.
- Do not delete original-compatible assets unless they are proven obsolete and replaced.

## 7. Required porting methodology

For every gameplay-critical class:

1. Locate the current 1.12.2 source under `src/main/java/**`.
2. Locate the original 1.7.10 reference under `thaumcraft_src/**` or decompile the original class with CFR.
3. Compare behavior before editing.
4. Adapt behavior to Forge 1.12.2 APIs.
5. Preserve names and data contracts where practical.
6. Run focused validation.

Do not rely on PRD phase text alone. The code and original reference are authoritative.

## 8. Current status model

This PRD intentionally separates “compile status” from “parity status”.

A class can be:

- Missing: no 1.12.2 implementation exists.
- Stubbed: compiles but returns placeholder behavior.
- Partial: core behavior exists but known mechanics are missing.
- Implemented: behavior is ported but not runtime-smoke-tested.
- Validated: behavior is implemented and verified through build plus runtime/manual checks.

Only “validated” should be considered closed for parity claims.

## 9. Phase overview

### Phase 0: Tooling and build foundation

Goal:

- Forge 1.12.2 Java 8 workspace builds consistently.

Expected status:

- Build baseline exists, but every run must verify locally. This is not a parity claim.

Validation:

- Docker image builds.
- Gradle tasks are discoverable.
- `compileJava` passes.
- `build` passes.

### Phase 1: API, bundled CCL, TrueType

Detailed gap analysis:

- [`docs/Stage1.md`](Stage1.md)

Goal:

- `thaumcraft.api.*`, `thaumcraft.codechicken.*`, and `truetyper.*` compile and preserve their intended role.

Validation:

- `compileJava`.
- `apiJar`.
- API jar contains `thaumcraft/api/**`.

Risk:

- Rendering helpers and raytracing/math helpers may compile but still be behavior-sensitive.

### Phase 2: Forge lifecycle, registration, config, networking

Detailed gap analysis:

- [`docs/Stage2.md`](Stage2.md)

Goal:

- Mod lifecycle and registries are connected through Forge 1.12.2 APIs.

Validation:

- `compileJava`.
- `runClient` smoke test when possible.
- Verify no duplicate/missing registry names.

Risk:

- Registry identity, packet ordering, GUI ids, and config keys are compatibility-sensitive.

### Phase 3: Core systems

Detailed gap analysis:

- [`docs/Stage3.md`](Stage3.md)

Goal:

- Aspects, aura/vis, wands, research, scan state, warp, potions, enchantments, and player capabilities work.

> **Current status tracked in `docs/GOAL_PROGRESS.md`.**

Known risk areas:

- Bauble vis storage and inventory vis consumption runtime scenarios.
- Runic Ring tick behavior versus original behavior.
- Research sync timing.
- Capability persistence and fresh-world player-data reload.

Acceptance:

- Existing working baselines remain intact.
- Remaining gaps are fixed or explicitly deferred.

### Phase 4: Blocks and tile entities

Detailed gap analysis:

- [`docs/Stage4.md`](Stage4.md)

Goal:

- Blocks and tile entities are placeable, persistent, interactive, and server-functional.

> **Current status tracked in `docs/GOAL_PROGRESS.md`.**

Known risk areas:

- Arcane Bore runtime/manual mining validation.
- `TileArcaneBoreBase` original-behavior verification.
- Thaumatorium recipe programming/content flow.
- Focal Manipulator upgrade UI/content flow.
- Infusion Matrix lifecycle and instability.
- Crucible interactions.
- Essentia transport.
- Tile NBT and sync.

Acceptance:

- Server-visible interactions are implemented before client GUI polish.
- GUI-dependent completion is deferred only where documented.

### Phase 5: Items, tools, armor, baubles, relics, wands, foci

Detailed gap analysis:

- [`docs/Stage5.md`](Stage5.md)

Goal:

- Items and equipment preserve original gameplay behavior.

> **Current status tracked in `docs/GOAL_PROGRESS.md`.**

Known risk areas:

- Focus cost consumption and world/entity scenarios.
- Focus visual feedback and FX, deferred to Phase 8.
- Bauble storage and tick behavior validation.
- Hover Harness flight/hover behavior.
- Tool and armor repairability broad parity.
- Primal Crusher behavior validation.
- Wand/focus upgrade costs and side effects.

Acceptance:

- Foci have server behavior, not only visual placeholders.
- Wand/bauble/relic behavior uses existing common-layer boundaries.
- Repairability matches original intent.

### Phase 6: Entities, mobs, bosses, golems

Detailed gap analysis:

- [`docs/Stage6.md`](Stage6.md)

Goal:

- Entities, AI, projectiles, bosses, golems, drops, sounds, and special behaviors are ported.

> **Current status tracked in `docs/GOAL_PROGRESS.md`.**

Known risk areas:

- Boss special attacks runtime validation.
- Pech trade, taming, pickup, and combat behavior.
- Golem AI behavior and interaction flows.
- Drops and sounds under runtime scenarios.
- Client renderers are Phase 8.

Acceptance:

- Server-visible entity behavior is fixed before claiming Phase 6 parity.
- Client rendering remains separate.

### Phase 7: World generation and Outer Lands

Detailed gap analysis:

- [`docs/Stage7.md`](Stage7.md)

Goal:

- Biomes, trees, structures, dimensions, maze generation, portals, persistence, and world data behave correctly.

> **Current status tracked in `docs/GOAL_PROGRESS.md`.**

Known risk areas:

- Outer Lands generation/runtime smoke tests.
- Room generator parity.
- Maze persistence.
- Biome color/debug overlay cosmetics.
- Structure query hooks.
- Portal teleport safety.

Acceptance:

- Runtime generation works in a new world.
- Deferred cosmetic issues are explicitly documented.

### Phase 8: Client GUI, rendering, particles, shaders

Detailed gap analyses:

- [`docs/Stage8-a.md`](Stage8-a.md) — client bootstrap, side separation, proxy/event/keybind boundaries.
- [`docs/Stage8-b.md`](Stage8-b.md) — client GUI screens and GUI resources.
- [`docs/Stage8-c.md`](Stage8-c.md) — tile entity renderers, tile/block models, render resources.
- [`docs/Stage8-d.md`](Stage8-d.md) — entity renderers, entity models, entity render resources.
- [`docs/Stage8-e.md`](Stage8-e.md) — particles, beams, bolts, shaders, FX packets/resources.

Goal:

- All core GUIs open and behave, all critical tile/entity renders work, particle/beam/bolt/shader systems are ported enough for visual parity.

> **Current status tracked in `docs/GOAL_PROGRESS.md`.**

Deliverables:

- GUI screens for core containers and item/entity interactions.
- Client proxy registration.
- Key bindings and client event handlers.
- Tile entity renderers.
- Entity renderers.
- Model classes.
- Particle engine.
- Beam and bolt effects.
- Shader/post-processing resources where supported by Forge 1.12.2.
- Required GUI/model/texture/lang/sound resources.

Risk:

- High client/server side separation risk.
- High visual regression risk.
- Runtime smoke testing is required.

### Phase 9: Recipes, research, and content registrations

Detailed gap analyses:

- [`docs/Stage9-a.md`](Stage9-a.md) — recipe registration foundation, vanilla/Forge recipes, smelting, aspect tags.
- [`docs/Stage9-b.md`](Stage9-b.md) — arcane crafting recipes and Arcane Workbench content flow.
- [`docs/Stage9-c.md`](Stage9-c.md) — infusion crafting/enchantment recipes and content flow.
- [`docs/Stage9-d.md`](Stage9-d.md) — crucible/alchemy recipes and alchemical content flow.
- [`docs/Stage9-e.md`](Stage9-e.md) — research categories/items/pages, Thaumonomicon content, unlock flows.

Goal:

- Crafting/research/content data is complete enough for progression parity.

Deliverables:

- Vanilla/Forge JSON recipes where appropriate.
- Arcane crafting recipes.
- Infusion recipes.
- Crucible recipes.
- Smelting/aspect tags.
- Research categories/items/pages.
- Thaumonomicon content references.
- Recipe/research unlock flows.
- Research note/content flow if required by original behavior.

Risk:

- Registry names, research keys, recipe ids, and GUI references must align.
- Content can compile while being unusable in game, so runtime/manual checks are required.

### Phase 10: Polish and compatibility

Goal:

- Final pass after gameplay, client, and content systems exist.

Allowed work:

- Sound verification.
- Config GUI if already scaffolded or required.
- Optional JEI integration only if it remains optional.
- Localization breadth.
- Performance tuning for completed systems.
- Crash/runtime smoke testing.
- Documentation/status updates.

Forbidden before parity:

- Adding polish while core gameplay/client/content parity remains broken.
- Adding mandatory optional dependencies.
- Broad cleanup.

## 10. Validation strategy

Use `./scripts/dev.sh validate` and related shortcuts. See `AGENTS.md` lines 133-160 for all supported commands and their semantics.

## 11. Runtime smoke checklist

For any successful parity claim, document which of these were run.

Server/common checks:

- New world loads without registry/config crash.
- Fresh world reloads after first save without NBT/capability crash.
- Wand can hold/use focus.
- Focus actions mutate world/entities only on server side.
- Vis costs and discounts are consumed correctly.
- Baubles tick/storage behavior works.
- Thaumometer scan updates research/scan state.
- Crucible/Infusion Matrix/Arcane Bore core interactions work where claimed.
- Boss/special mobs spawn and execute special behavior.
- Golems execute core AI.
- Outer Lands portal/dimension/maze runtime works where claimed.

Client checks:

- Core GUIs open without client crash.
- Tile entity renderers do not crash when chunks load.
- Entity renderers do not crash for registered mobs/projectiles.
- Particles/beams/bolts render or fail gracefully.
- Shaders either work or are explicitly disabled/deferred with reason.
- Missing texture/model errors are investigated.

Content checks:

- Recipes register.
- Research categories/items/pages register.
- Thaumonomicon pages reference valid content.
- Progression can unlock and use implemented recipes.

## 12. Definition of done

A phase or checkpoint is done only when:

- implementation matches original behavior as closely as Forge 1.12.2 allows;
- public contracts are preserved;
- build validation passes;
- runtime/manual smoke checks are documented where relevant;
- remaining limitations are explicit;
- docs are updated after code validation, not before.
