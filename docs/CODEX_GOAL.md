# Goal Manifest: Boss and Special Mob Server Parity

## Objective

Complete the remaining deferred pre-Phase8 boss/special mob server parity.

Current state:

- Portable Hole and Warding wrapper systems are already implemented.
- P0 focus/block wrapper blockers are closed.
- Boss/special mob parity remains deferred.
- Offline `.thaum/.thaumbak` research migration remains deferred and is out of scope for this checkpoint.
- Phase 8 client work is out of scope.

Target state:

- Cultist Leader has original-compatible equipment, ranged attack, and nearby cultist buff behavior.
- Eldritch Golem has original-compatible headless transition and beam/ranged behavior.
- Eldritch Warden has original-compatible ranged attack and frenzy/teleport/screech behavior.
- Pech death loot is original-compatible.
- Entity registry names, NBT/data parameters, AI task structure, drops, and server behavior remain compatible.
- No client rendering, GUI, recipe, research, Portable Hole, Warding, or Hover Harness work is included.

## Scope

Allowed files:

- `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistLeader.java`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchWarden.java`
- `src/main/java/thaumcraft/common/entities/monster/EntityPech.java`
- `src/main/java/thaumcraft/common/entities/projectile/EntityGolemOrb.java`
- `src/main/java/thaumcraft/common/entities/projectile/EntityEldritchOrb.java`
- `src/main/java/thaumcraft/common/entities/projectile/EntityPechBlast.java`
- related AI/helper/loot/config classes only if original behavior requires them
- `docs/REPAIR.md` after validation

Out of scope:

- `thaumcraft.client.*`
- Phase 8 GUI/renderers/FX/shaders
- `ConfigRecipes`
- `ConfigResearch`
- Phase 9 recipes/research
- Portable Hole/Warding files
- Offline `.thaum/.thaumbak` migration
- Hover Harness flight
- JEI/config GUI/localization polish
- dependency upgrades
- broad formatting

## Required reference workflow

Before changing each class, inspect the original 1.7.10 behavior via CFR:

- `EntityCultistLeader`
- `EntityEldritchGolem`
- `EntityEldritchWarden`
- `EntityPech`
- `EntityGolemOrb`
- `EntityEldritchOrb`
- any original loot/helper classes used by Pech death drops

Do not infer boss behavior from comments alone.

## Implementation plan

### Checkpoint 1: Behavior mapping

- Decompile/read original classes.
- Map current TODOs to original methods.
- Identify NBT/data watcher fields that must be preserved.
- Identify whether existing projectile classes are sufficient or need small corrections.

Validation:

- `git status --short`
- focused `rg` scan for boss/Pech TODOs

### Checkpoint 2: Cultist Leader

- Add original-compatible equipment.
- Implement ranged GolemOrb attack.
- Implement nearby cultist buff behavior.
- Preserve AI task structure unless original behavior requires adjustment.

Validation:

- `compileJava`
- manual spawn scenario notes

### Checkpoint 3: Eldritch Golem

- Implement headless transition behavior.
- Persist/sync headless state if original behavior requires it.
- Implement beam/ranged attack through existing projectile/helper path where possible.
- Preserve boss attributes and existing AI.

Validation:

- `compileJava`
- manual spawn/damage scenario notes

### Checkpoint 4: Eldritch Warden

- Implement EldritchOrb ranged attack.
- Implement frenzy/teleport/screech behavior.
- Preserve target behavior and existing sounds unless original behavior requires changes.

Validation:

- `compileJava`
- manual spawn/combat scenario notes

### Checkpoint 5: Pech death loot

- Implement original-compatible death loot.
- Preserve existing trading/taming/pickup/combat behavior.
- Do not move Pech GUI/client work into this checkpoint.

Validation:

- `compileJava`
- manual Pech death/drop scenario notes

### Checkpoint 6: Final validation and docs

Run:

- `git status --short`
- `git diff --stat`
- `git diff --name-only`
- Docker `compileJava`
- Docker `build`
- `git diff --check`
- focused placeholder scan for touched files

Update `docs/REPAIR.md` only after validation.

Commit only if scoped and validated:

- `port: restore boss and special mob server parity`

If blocked, do not commit.

## Acceptance criteria

Functional:

- Cultist Leader no longer has ranged/equipment/aura TODO behavior.
- Eldritch Golem no longer has headless/beam TODO behavior.
- Eldritch Warden no longer has ranged/frenzy TODO behavior.
- Pech death loot is no longer a stub.
- Existing entity registration and spawn behavior remain compatible.
- Existing projectile behavior is preserved unless corrected against original reference.

Architecture:

- Server behavior remains in `thaumcraft.common.*`.
- No client Phase 8 files are touched.
- No recipe/research files are touched.
- No Portable Hole/Warding files are touched.
- No public API change is introduced.

Validation:

- `compileJava` passes.
- `build` passes or pre-existing/environment failure is documented.
- Final report lists original reference classes inspected.
- Final report lists manual runtime scenarios performed or skipped with reason.

Diff:

- Diff stays inside stated scope.
- No generated files are committed.
- No `thaumcraft_src/**` changes.
- No original jar changes.
- No broad formatting-only changes.
