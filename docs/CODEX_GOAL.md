# Goal Manifest: Portable Hole and Warding Server Wrapper Parity

## Objective

Implement the deferred server/common block-wrapper systems needed by `FocusPortableHole` and `FocusWarding`.

Current state:

- `FocusPortableHole` is deferred because no original-compatible `BlockHole` + `TileHole` restoration system exists.
- `TileHole` exists only as a placeholder.
- `FocusWarding` is deferred because no original-compatible arbitrary block wrapper equivalent to `blockWarded` + `TileWarded` exists.
- There is no current registered `BlockHole`/`BlockWarded` implementation in the active 1.12.2 source.

Target state:

- `FocusPortableHole` creates temporary pass-through holes using a registered block/tile system.
- The hole tile stores original block state, tile data if safely supported, side/direction, duration/expiry, owner/caster data if original behavior requires it, and restores safely.
- `FocusWarding` wraps valid target blocks in a warded wrapper block/tile.
- The warded tile stores owner and original block state/data needed to render/drop/restore/protect the block.
- Public API, config keys, NBT semantics, registry identity, and existing behavior remain compatible.
- No client Phase 8 GUI/rendering work is started.

## Required reference workflow

Before implementation:

- Inspect current 1.12.2 files:
  - `FocusPortableHole.java`
  - `FocusWarding.java`
  - `TileHole.java`
  - `ConfigBlocks.java`
  - block registration/event registration paths
  - tile registration paths
  - relevant block utilities
  - relevant wand/vis helpers

- Decompile/read original 1.7.10 classes from `Thaumcraft-1.7.10-4.2.3.5.jar` using CFR:
  - original `FocusPortableHole`
  - original `BlockHole`
  - original `TileHole`
  - original `FocusWarding`
  - original `BlockWarded`
  - original `TileWarded`
  - any original helper classes they call

Do not rely on memory or PRD text for behavior.

## Scope

Allowed paths:

- `src/main/java/thaumcraft/common/items/wands/foci/FocusPortableHole.java`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusWarding.java`
- `src/main/java/thaumcraft/common/tiles/TileHole.java`
- new `src/main/java/thaumcraft/common/tiles/TileWarded.java` if required
- new `src/main/java/thaumcraft/common/blocks/BlockHole.java` if required
- new `src/main/java/thaumcraft/common/blocks/BlockWarded.java` if required
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java`
- existing block/tile registration paths
- existing `BlockUtils`/`EntityUtils` only if a small helper is required
- minimal `docs/REPAIR.md` status update after validation

Out of scope:

- `thaumcraft.client.*`
- Phase 8 GUI/renderers/FX/shaders
- `ConfigRecipes`
- `ConfigResearch`
- Phase 9 recipes/research
- bosses/special mobs
- Pech loot
- offline `.thaum`/`.thaumbak` migration
- JEI
- config GUI
- broad formatting
- dependency upgrades
- public API signature changes
- registry/config/NBT renames

## Implementation checkpoints

### Checkpoint 1: Original behavior mapping

- Decompile/read original Portable Hole and Warding classes.
- Identify exact NBT fields, duration behavior, restoration behavior, owner checks, target validity checks, and blacklist/edge-case behavior.
- Inspect current registration lifecycle for blocks and tile entities.
- Do not modify code yet except optional notes in final report.

Validation:

- `git status --short`
- `rg -n "TileHole|BlockHole|TileWarded|BlockWarded|FocusPortableHole|FocusWarding" src/main/java thaumcraft_src docs`

Stop if original behavior cannot be determined.

### Checkpoint 2: Implement BlockHole/TileHole baseline

- Implement registered `BlockHole` if absent.
- Expand `TileHole` from placeholder into original-compatible temporary restoration tile.
- Store original block state safely for 1.12.2.
- Restore block on expiry/removal/server tick according to original behavior.
- Avoid corrupting tile entities; if tile entity restoration is ambiguous, document blocker rather than guessing.
- Wire registration without changing unrelated block registry names.

Validation:

- `compileJava`
- `processResources` if resources changed
- focused manual scenario notes

### Checkpoint 3: Wire FocusPortableHole

- Replace deferred no-op with server-side targeting and hole placement.
- Consume vis through existing wand logic.
- Respect side checks and block modification checks.
- Do not mutate world on client side.
- Add safe failure behavior.

Validation:

- `compileJava`
- manual scenario:
  - cast on normal solid block;
  - hole appears;
  - player can pass/use intended behavior;
  - original block restores after duration;
  - invalid target fails safely;
  - insufficient vis fails safely.

### Checkpoint 4: Implement BlockWarded/TileWarded baseline

- Implement registered `BlockWarded` if absent.
- Implement `TileWarded`.
- Store owner identity.
- Store original block state and light/metadata-equivalent data required by original behavior.
- Enforce owner/protection behavior according to original behavior.
- Preserve drops/restoration semantics where original-compatible and safe.

Validation:

- `compileJava`
- focused manual scenario notes

### Checkpoint 5: Wire FocusWarding

- Replace deferred no-op with server-side warding behavior.
- Consume vis through existing wand logic.
- Respect target validity, owner, world modification, and protected-block checks.
- Do not mutate client world.
- Do not ward forbidden blocks.

Validation:

- `compileJava`
- manual scenario:
  - ward normal block;
  - owner can interact/break if original allows;
  - non-owner behavior matches original;
  - block restores/drops safely;
  - invalid target fails safely;
  - insufficient vis fails safely.

### Checkpoint 6: Final validation and commit

Run:

- `git status --short`
- `git diff --stat`
- `git diff --name-only`
- Docker `compileJava`
- Docker `build` if practical
- Docker `apiJar devJar` if practical
- placeholder scan for touched scope

Commit only if scoped and validated:

- suggested commit message:
  - `port: implement portable hole and warding block wrappers`

If blocked, do not commit; stop with final report.

## Acceptance criteria

Functional:

- `FocusPortableHole` is no longer a no-op unless blocked by documented original behavior ambiguity.
- `FocusWarding` is no longer a no-op unless blocked by documented original behavior ambiguity.
- Hole restoration does not lose block state in common valid cases.
- Warded blocks preserve original block identity/state in common valid cases.
- Vis is consumed only on successful server-side action.
- Invalid targets fail safely.
- Existing foci/wand behavior remains unchanged.

Architecture:

- Server/common behavior stays in `thaumcraft.common.*`.
- No client Phase 8 code is touched.
- No recipes/research code is touched.
- Public API is unchanged.
- Registry additions are minimal and named consistently with existing Thaumcraft registry style.
- No speculative abstraction is introduced.

Validation:

- `compileJava` passes.
- `build` passes or blocker/pre-existing failure is documented.
- Final report lists exact commands and results.
- Final report lists original reference classes inspected.
- Final report lists manual scenarios performed or skipped with reason.

Diff:

- Diff stays inside stated scope.
- No generated output is committed.
- No `thaumcraft_src/**` changes.
- No `Thaumcraft-1.7.10-4.2.3.5.jar` changes.
- No broad formatting-only changes.
