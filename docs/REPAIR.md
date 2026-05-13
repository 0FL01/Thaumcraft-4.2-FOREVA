# Thaumcraft 4.2.3.5 -> Forge 1.12.2 Port Repair Backlog

## 1. Purpose

This document is the active repair backlog for closing parity gaps. It is intentionally narrower than `docs/PRD.md`.

Use this file to decide what the next Codex `/goal` run should implement.

Do not start Phase 8 client work until the pre-Phase8 gates below are fixed or explicitly deferred with evidence.

## 2. Baseline rules

Before changing code:

- Run `git status --short`.
- Read `AGENTS.md`.
- Read `docs/PRD.md`.
- Read this file.
- Read `docs/CODEX_GOAL.md`.
- Run or document baseline validation.
- Inspect original 1.7.10 behavior for every gameplay-critical touched class.

A compile-passing class is not automatically parity-complete.

## 3. Baseline validation

Preferred baseline command:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user "$(id -u):$(id -g)" \
      --entrypoint ./gradlew \
      thaumcraft-dev compileJava

If Docker image does not exist:

    docker build -t thaumcraft-dev .

If a fresh workspace is not initialized:

    docker run --rm \
      -v "$(pwd):/workspace/thaumcraft" \
      -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
      --user "$(id -u):$(id -g)" \
      --entrypoint ./gradlew \
      thaumcraft-dev setupDecompWorkspace

## 4. Closed or do-not-reopen without evidence

Do not re-audit these as blockers unless current code contradicts them:

- Sound registration baseline exists.
- Major projectile baseline exists.
- Main AI source baseline exists.
- Outer Lands runtime hookup baseline exists.
- Container hard-locks were previously remediated.
- Phase 3 core baseline exists for aura persistence, wand centi-vis units, discounts, and no passive wand recharge.
- Research/potion baseline exists for online capability-backed lookup and restored server potion effects.
- Crucible baseline exists for item ingestion, water interaction, spill behavior, and aspect container exposure.
- Major tile entity baseline exists for several machines, but full machine gameplay remains separate.
- Infusion Matrix server lifecycle baseline exists.

These statements must still be verified locally before being used as final parity claims.

## 5. Pre-Phase8 gate: P0 blockers

P0 items block Phase 8 unless explicitly deferred in the final report with a reason.

### P0.1 Remaining focus server actions

Problem:

Six focus items still have no-op or placeholder server behavior. These are server/common gameplay features, not Phase 8 visual/client work.

Target files:

- `src/main/java/thaumcraft/common/items/wands/foci/FocusPortableHole.java`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusTrade.java`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusPech.java`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusHellbat.java`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusExcavation.java`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusWarding.java`

Related files:

- `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java`
- `src/main/java/thaumcraft/common/items/wands/WandManager.java`
- projectile/entity/helper classes needed by original behavior
- relevant packet/client FX hooks only if server behavior requires a notification

Required method:

- Compare each focus against the original 1.7.10 reference.
- Implement server-side behavior first.
- Preserve existing cost units and focus upgrade semantics.
- Keep visual-only feedback deferred to Phase 8 if needed.
- Add side checks to avoid client-only world mutation.
- Keep public API stable.

Exit criteria:

- Each focus has server behavior or a documented explicit blocker.
- Vis costs are consumed through existing wand logic.
- Safe side checks are present.
- `compileJava` passes.
- Focus-specific manual scenario is documented in final report.

### P0.2 Arcane Bore server mining loop decision

Problem:

Arcane Bore has baseline inventory/orientation/NBT behavior, but full block scanning/digging/mining loop may still be partial.

Target files:

- `src/main/java/thaumcraft/common/tiles/TileArcaneBore.java`
- `src/main/java/thaumcraft/common/tiles/TileArcaneBoreBase.java`
- related block/item/render-independent helpers only if required

Required method:

- Inspect original 1.7.10 bore behavior.
- Decide whether full bore gameplay blocks Phase 8.
- If it blocks, implement server mining loop.
- If it is deferred, document why it is safe to move to Phase 8 before completion.

Exit criteria:

- Either full server mining behavior works, or deferral is explicitly documented.
- `compileJava` passes.
- If implemented, a manual scenario is documented.

## 6. Pre-Phase8 gate: P1 blockers

P1 items should be fixed before Phase 8 unless explicitly deferred with evidence.

### P1.1 Baubles, relics, and wand integration

Problems:

- Runic ring tick behavior needs verification/implementation.
- Vis amulet storage behavior needs verification/implementation.
- Thaumometer scan action needs verification/implementation.
- Inventory vis consumption needs to include bauble storage where original behavior requires it.

Target files:

- `src/main/java/thaumcraft/common/items/baubles/ItemRingRunic.java`
- `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java`
- `src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java`
- `src/main/java/thaumcraft/common/items/wands/WandManager.java`
- `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java`
- relevant capability/research helper classes

Exit criteria:

- Bauble tick/storage behavior works server-side.
- Wand inventory vis consumption accounts for allowed sources.
- Thaumometer scan action updates scan/research state.
- Existing wand discount/centi-vis behavior remains unchanged.
- `compileJava` passes.

### P1.2 Research compatibility and enchantments

Problems:

- Offline `.thaum` and `.thaumbak` compatibility may not be original-compatible.
- Frugal enchantment applicability may not match focus-specific original behavior.

Target files:

- `src/main/java/thaumcraft/common/lib/research/ResearchManager.java`
- `src/main/java/thaumcraft/common/lib/research/PlayerKnowledge.java`
- `src/main/java/thaumcraft/common/lib/enchantment/EnchantmentFrugal.java`
- related API/crafting/research helper classes only if required

Exit criteria:

- Original-compatible offline research/aspect loading exists or deferral is documented.
- Frugal applies only where original behavior allows.
- Public API remains compatible.
- `compileJava` passes.

### P1.3 Boss and special mob behavior

Problems:

- Cultist Leader special behavior needs verification/implementation.
- Eldritch Golem beam/headless behavior needs verification/implementation.
- Eldritch Warden ranged/frenzy behavior needs verification/implementation.
- Inhabited Zombie death spawn needs verification/implementation.
- Pech loot/trade behavior needs verification/implementation.

Target files:

- `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistLeader.java`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchWarden.java`
- `src/main/java/thaumcraft/common/entities/monster/EntityInhabitedZombie.java`
- `src/main/java/thaumcraft/common/entities/monster/EntityPech.java`
- related projectile/AI/loot/helper classes only if required

Exit criteria:

- Server-visible special behavior is implemented or explicitly deferred.
- Entity registration and existing AI behavior remain stable.
- `compileJava` passes.
- Manual spawn/behavior scenario is documented.

### P1.4 Tools, armor, and repairability

Problems:

- Primal Crusher inheritance/behavior and repair check need verification.
- Multiple tools/armor classes may still return incorrect placeholder repair checks.

Target files:

- `src/main/java/thaumcraft/common/items/equipment/ItemPrimalCrusher.java`
- `src/main/java/thaumcraft/common/items/equipment/**`
- `src/main/java/thaumcraft/common/items/armor/**`
- relevant `IRepairable` and `IRepairableExtended` implementations

Exit criteria:

- Repairability matches original behavior.
- Placeholder always-false checks are removed where incorrect.
- No unrelated equipment behavior changes.
- `compileJava` passes.

## 7. Phase 8 client backlog

Start only after P0 is closed or explicitly deferred.

Target areas:

- `src/main/java/thaumcraft/client/ClientProxy.java`
- `src/main/java/thaumcraft/client/gui/**`
- client event handlers
- key bindings
- GUI screens
- tile entity special renderers
- entity renderers
- model classes
- particle engine
- beam/bolt effects
- shader/post-processing support
- GUI/model/texture/lang/sound resources

Required first step:

- Inventory current `thaumcraft.client.*` implementation.
- Compare against original `thaumcraft_src/thaumcraft/client/**`.
- Create a small class-by-class client backlog.
- Implement in groups:
  - proxy registrations;
  - core GUIs;
  - TESRs;
  - entity renderers;
  - particles/beams/bolts;
  - shaders/resources.

Validation:

- `compileJava`.
- `processResources` after resource changes.
- `runClient` if display is available.
- Manual GUI/render smoke scenarios.

## 8. Phase 9 content backlog

Start after required GUI/client pathways are usable enough to verify content.

Target areas:

- `src/main/java/thaumcraft/common/config/ConfigRecipes.java`
- `src/main/java/thaumcraft/common/config/ConfigResearch.java`
- `src/main/java/thaumcraft/common/lib/crafting/**`
- `src/main/java/thaumcraft/common/lib/research/**`
- `src/main/resources/assets/thaumcraft/recipes/**`
- research/Thaumonomicon resource references
- aspect tag registration

Required first step:

- Inventory current recipe/research registration.
- Compare against original reference.
- Separate missing content from broken registration mechanics.

Exit criteria:

- Recipes register.
- Research registers.
- Thaumonomicon references valid content.
- Critical progression path is manually smoke-tested.

## 9. Phase 10 polish backlog

Allowed only after gameplay, client, and content baselines exist.

Possible items:

- JEI integration if optional.
- Config GUI if scaffolded or necessary.
- Localization breadth.
- Sound completeness verification.
- Optional compatibility.
- Performance tuning.
- Crash test pass.

Do not let polish work hide unresolved core parity gaps.

## 10. Required final report per checkpoint

Every `/goal` run must end with:

- Summary of changes.
- Files modified.
- Original reference classes inspected.
- Architecture before/after.
- Acceptance checklist.
- Validation commands and results.
- Runtime/manual checks.
- Known limitations.
- Blockers.
- Suggested next checkpoint.

## 11. Update policy

After a blocker is fixed:

- Update this file to move it from active blocker to closed status.
- Keep the update factual.
- Include validation evidence.
- Do not mark a phase done unless runtime/manual evidence supports it.
