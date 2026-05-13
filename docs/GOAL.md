# Goal Manifest: Завершение порта Thaumcraft 4.2.3.5 до паритета Minecraft Forge 1.12.2

## 1. Objective

Цель будущего запуска `/goal`: завершить порт `Thaumcraft 4.2.3.5` с Minecraft `1.7.10` на Minecraft Forge `1.12.2` до максимально достижимого поведенческого паритета, не ломая существующую структуру проекта, публичный API и внешние контракты.

Текущая структура проекта уже содержит частично портированный Forge-мод:

* основная реализация живёт в `src/main/java/thaumcraft/**`;
* оригинальные распакованные reference-файлы живут в `thaumcraft_src/**`;
* документация фиксирует фазовый план порта и текущие пробелы в `docs/PRD.md` и `docs/REPAIR.md`;
* build основан на ForgeGradle/Gradle/Java 8.

Целевая структура после выполнения:

* documented pre-Phase8 server blockers закрыты или явно задокументированы как осознанно deferred;
* Phase 8 client GUI/rendering/FX/shader work реализован в существующих `thaumcraft.client.*` boundaries;
* Phase 9 recipes/research/content registrations реализованы через Forge 1.12.2-compatible registration flow;
* Phase 10 polish выполняется только там, где это нужно для parity/stability, без широкого cleanup;
* `thaumcraft.api.*` остаётся совместимым API addon-контрактом;
* пакетные boundaries `thaumcraft.api.*`, `thaumcraft.common.*`, `thaumcraft.client.*`, `thaumcraft.codechicken.*`, `truetyper.*` сохраняются;
* build, resource processing, API/dev jars и runtime smoke checks проходят или pre-existing failures документированы с доказательствами.

Состояние `done` означает: мод компилируется, собирается, запускается в clean Forge 1.12.2 client/server environment, documented parity gaps устранены в scope, public contracts не изменены без явного обоснования, итоговый diff ограничен портированием недостающего поведения.

## 2. User Context

Пользовательский запрос в инженерных терминах: завершить WIP-порт `Thaumcraft 4.2` до полного функционального паритета с оригинальной версией `1.7.10`, насколько это позволяет API Minecraft Forge `1.12.2`.

Явные требования:

* не выполнять рефакторинг прямо сейчас;
* не менять production-code при генерации этого манифеста;
* не создавать и не изменять `GOAL.md`;
* не писать результат в файл;
* будущий `/goal` должен работать по конкретному, bounded, testable плану;
* избегать широких cleanup-изменений;
* сохранять existing behavior и публичные контракты.

Предполагаемый intent:

* получить actionable roadmap для Codex CLI `/goal`;
* ограничить будущую работу рамками documented port parity, а не “улучшить всё”;
* заставить будущий запуск сначала закрыть известные блокеры и только потом двигаться в client/content phases.

Non-goals:

* не портировать проект на другую версию Minecraft;
* не менять Forge version, Gradle version, Java version или Baubles dependency без blocker-level причины;
* не переписывать архитектуру на современный Forge/Fabric стиль;
* не удалять исторический reference source;
* не менять mod identity, registry names, NBT/data contracts, config keys или API shape ради удобства.

## 3. Repository Findings

Репозиторий `0FL01/Thaumcraft-4.2-FOREVA` описан как WIP “Full port Thaumcraft 4.2 on minecraft 1.12.2”; root listing показывает `docs`, `src/main`, `thaumcraft_src`, `AGENTS.md`, `Dockerfile`, Gradle wrapper/config files и оригинальный `Thaumcraft-1.7.10-4.2.3.5.jar`. GitHub language summary показывает Java as dominant language. ([GitHub][1])

Тип проекта: Java 8 Minecraft Forge mod. Build-система: Gradle с ForgeGradle `2.3-SNAPSHOT`, Forge `1.12.2-14.23.5.2847`, mappings `stable_39`, `sourceCompatibility = targetCompatibility = '1.8'`. Единственная явно объявленная compile dependency в `build.gradle` — Baubles через CurseMaven; build также определяет `apiJar` для `thaumcraft/api/**` и `devJar` для IDE/debug output. ([GitHub][2])

Документация для агентов фиксирует stack: Java 8, MinecraftForge 1.12.2, Gradle, Baubles hard dependency, bundled CodeChicken Lib; также требует держать original package names, original field/method names, CFR-first workflow и `BUILD SUCCESSFUL` после micro-step. ([GitHub][3])

Основные модули:

* `src/main/java/thaumcraft/api/**`: публичный addon API и API jar boundary.
* `src/main/java/thaumcraft/codechicken/lib/**`: bundled CCL-like math/render helper library.
* `src/main/java/thaumcraft/truetyper/**`: TrueType rendering support.
* `src/main/java/thaumcraft/common/**`: server/common gameplay, blocks, items, entities, tile entities, config, networking, research, worldgen.
* `src/main/java/thaumcraft/client/**`: client proxy, GUI/rendering/FX work.
* `src/main/resources/assets/thaumcraft/**`: assets, models, sounds, lang, recipes/resources.
* `thaumcraft_src/**`: original unpacked JAR reference; treat as read-only parity source.

Architecture documented in `docs/PRD.md`: port order is `thaumcraft.api` first, then bundled `thaumcraft.codechicken.*`, `truetyper`, core systems, common content, client content, crafting/research, then polish/JEI. External dependencies documented there include Baubles, MinecraftForge, bundled CodeChickenLib, optional JEI and optional OptiFine detection. ([GitHub][4])

Current status is not “almost done.” `AGENTS.md` says Phase 8 client GUI/rendering and Phase 9 recipes/research are still not done; Phase 3-7 areas are partial; `docs/REPAIR.md` says `compileJava` passes but phases 3-7 are not cleanly closed and pre-Phase8 server gameplay/runtime blockers remain. ([GitHub][3])

Known pre-Phase8 blockers from `docs/REPAIR.md`:

* Arcane Bore mining loop still partial.
* Six focus server actions are no-op or incomplete: `FocusPortableHole`, `FocusTrade`, `FocusPech`, `FocusHellbat`, `FocusExcavation`, `FocusWarding`.
* Baubles/relic/wand integration gaps: runic ring tick, vis amulet storage, thaumometer scan action, inventory vis consumption ignoring bauble storage.
* Research/enchantment gaps: offline `.thaum`/`.thaumbak` compatibility and Frugal applicability.
* Boss/special mob TODOs: cultist leader, eldritch golem, eldritch warden, inhabited zombie, Pech loot.
* Tools/armor repairability gaps, including Primal Crusher and repair checks in equipment/armor packages. ([GitHub][5])

Phase 8 scope from PRD is large and client-side: GUIs, 52 TESRs, 42 entity renderers, 48 model classes, 22 FX classes, beam/bolt effects and shader system. Phase 9 scope is recipe/research/content registration; Phase 10 includes JEI/config GUI/sounds/localization/compat/performance/crash testing. ([GitHub][4])

Tests/lint/format/CI:

* No dedicated test framework, lint task, formatter task or CI workflow was found in the visible root/build docs.
* There is no documented `src/test` workflow in the inspected docs.
* Validation is primarily Gradle compile/build plus Forge runtime smoke testing.
* `compileJava` is documented as passing baseline in `docs/REPAIR.md`.
* Exact absence of hidden CI/test files must still be verified in a local checkout with `find . -maxdepth ...` before future `/goal` edits.

Public APIs and external contracts that must not break:

* `thaumcraft.api.*` signatures, package names and behavior exposed via `apiJar`.
* Mod metadata/resources, including `mcmod.info`, mod id/registry naming conventions and asset paths.
* Forge registry names for blocks/items/entities/potions/enchantments/biomes/sounds/recipes.
* Existing NBT keys for wands, foci, tile entities, player knowledge/warp/capabilities, world data and Outer Lands maze/dimension state.
* Existing network packet IDs/registration ordering unless a compatibility-preserving migration is required.
* Existing config keys and defaults in `thaumcraft.common.config.*`.
* Baubles integration behavior and slot semantics.
* Original package boundaries and field/method naming conventions used for traceability.

## 4. Current Problem

Текущая проблема не в том, что проекту нужен общий refactor. Проблема в том, что порт уже частично разложен по правильным модулям, но несколько critical parity paths остаются неполными, а документация прямо фиксирует server-visible blockers перед Phase 8.

Боли текущего дизайна/состояния:

* часть gameplay classes существует как compiling stub, placeholder или partial baseline;
* common/server behavior, client rendering and research/content phases разделены по документации, но фактические gaps пересекаются через shared systems: foci depend on wand/vis/bauble logic, GUIs depend on containers/tile entities, research depends on recipes and player knowledge;
* риск accidental behavior changes высокий, потому что многие классы портируются из 1.7.10 decompiled reference с сохранением names/semantics;
* coupling между Forge lifecycle, registries, capabilities, NBT и assets делает широкую перепись опасной;
* отсутствуют automated behavioral tests, поэтому validation должен опираться на compile/build/runtime smoke checks и focused manual scenario checklist.

Почему этот refactor/port completion имеет смысл:

* текущий project intent — full port parity, а не новая архитектура;
* documented blockers показывают конкретные недостающие поведенческие paths;
* завершение по фазам уменьшает риск: сначала common/server parity, затем client parity, затем recipes/research, затем polish.

Поведение, которое должно остаться неизменным:

* все уже работающие Phase 0-7 baselines;
* existing registry names and mod identity;
* existing API jar output;
* existing Baubles/Forge integration;
* existing NBT and save compatibility where already implemented;
* existing resource paths and localization keys unless parity requires adding missing ones.

## 5. Target End State

Target module boundaries:

* `thaumcraft.api.*`: remains public API only. No implementation-heavy logic should move into API unless original API requires it.
* `thaumcraft.common.*`: authoritative server/common gameplay logic: foci actions, wand/vis consumption, baubles/relic behavior, tile entity logic, entities, research state, recipes registration, world/dimension behavior.
* `thaumcraft.client.*`: client-only GUI, renderers, TESRs, entity renderers, particle engine, shaders, model registration, visual/sound feedback. No server gameplay decisions should move here.
* `src/main/resources/assets/thaumcraft/**`: blockstates, models, textures, lang, sounds, recipes and shader/resource files required by client/content parity.
* `thaumcraft_src/**` and `Thaumcraft-1.7.10-4.2.3.5.jar`: read-only reference inputs.

Target responsibilities:

* Foci server actions live in each `Focus*.java` class with shared helper logic only if at least two foci need the exact same side-safe behavior.
* Wand/vis/bauble accounting lives in `WandManager`, `ItemWandCasting` and relevant bauble/relic item classes, not duplicated per focus.
* Tile entity server loops live in their tile classes; containers/GUIs only expose state and user interaction.
* Client render code lives under `thaumcraft.client.*` and uses existing CCL/TrueType/helpers where intended.
* Recipes and research registrations live in existing config/research/crafting boundaries and use Forge 1.12.2 registry-compatible flows.
* Documentation status may be updated only to reflect completed work; it must not be used as a substitute for implementation.

Data flow after completion:

* Player action → item/focus/common handler → wand/vis/bauble/capability state → world/entity/tile effect → packet/client feedback where needed.
* Server tile/entity update → persistent NBT/capability/world data → client sync packet/render state.
* Registry initialization → config/registration classes → Forge registries/resources → runtime gameplay.
* Research/recipe content → Forge recipe registry/custom Thaumcraft recipe registries → Thaumonomicon/research pages/GUI references.

What should be removed/replaced:

* no-op placeholder focus server actions;
* TODO/TBD returns that block documented parity;
* obsolete client stubs once real GUI/render/FX implementations exist;
* dead helper methods only after all callers migrate and build proves they are unused.

What must stay unchanged:

* public `thaumcraft.api.*` package/signature compatibility;
* mod id and registry identity;
* Gradle/Forge/Baubles versions unless future `/goal` hits a hard blocker and documents it;
* original package names and field/method traceability;
* original reference sources and jar.

## 6. Scope

### In scope

Primary scope is documented parity completion, not broad cleanup.

Pre-Phase8 server/common parity scope:

* `src/main/java/thaumcraft/common/items/wands/foci/FocusPortableHole.java`
* `src/main/java/thaumcraft/common/items/wands/foci/FocusTrade.java`
* `src/main/java/thaumcraft/common/items/wands/foci/FocusPech.java`
* `src/main/java/thaumcraft/common/items/wands/foci/FocusHellbat.java`
* `src/main/java/thaumcraft/common/items/wands/foci/FocusExcavation.java`
* `src/main/java/thaumcraft/common/items/wands/foci/FocusWarding.java`
* `src/main/java/thaumcraft/common/items/wands/WandManager.java`
* `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java`
* `src/main/java/thaumcraft/common/items/baubles/ItemRingRunic.java`
* `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java`
* `src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java`
* `src/main/java/thaumcraft/common/lib/research/ResearchManager.java`
* `src/main/java/thaumcraft/common/lib/research/PlayerKnowledge.java`
* `src/main/java/thaumcraft/common/lib/enchantment/EnchantmentFrugal.java`
* `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistLeader.java`
* `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java`
* `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchWarden.java`
* `src/main/java/thaumcraft/common/entities/monster/EntityInhabitedZombie.java`
* `src/main/java/thaumcraft/common/entities/monster/EntityPech.java`
* `src/main/java/thaumcraft/common/items/equipment/ItemPrimalCrusher.java`
* `src/main/java/thaumcraft/common/items/equipment/**`
* `src/main/java/thaumcraft/common/items/armor/**`
* `src/main/java/thaumcraft/common/tiles/TileArcaneBore.java`
* related helpers in `src/main/java/thaumcraft/common/lib/**`, only when required by the above classes.

Phase 8 client parity scope:

* `src/main/java/thaumcraft/client/**`
* GUI classes under `thaumcraft.client.gui`
* client proxy registration paths
* render/model/TESR/entity renderer/particle/shader classes that are missing or stubbed
* `src/main/resources/assets/thaumcraft/textures/**`
* `src/main/resources/assets/thaumcraft/models/**`
* `src/main/resources/assets/thaumcraft/blockstates/**`
* `src/main/resources/assets/thaumcraft/gui/**`
* `src/main/resources/assets/thaumcraft/sounds.json`
* `src/main/resources/assets/minecraft/shaders/**`, only where documented shader parity requires it.

Phase 9 recipes/research/content scope:

* `src/main/resources/assets/thaumcraft/recipes/**`
* `src/main/java/thaumcraft/common/lib/research/**`
* `src/main/java/thaumcraft/common/lib/crafting/**`
* recipe/research registration classes under `src/main/java/thaumcraft/common/config/**` or existing registration modules
* Thaumonomicon/research page references needed for content parity.

Phase 10 limited polish scope:

* sound registration verification in existing `TCSounds`/config paths;
* config GUI only if already scaffolded or clearly required by docs;
* optional mod compatibility only if existing code already references it;
* performance/culling fixes for systems completed in this run;
* JEI integration only if it remains optional and does not force an unrelated dependency/version change.

Reference/read-only scope:

* `thaumcraft_src/**`
* `Thaumcraft-1.7.10-4.2.3.5.jar`

These may be read/decompiled/compared, but not modified.

Documentation scope:

* `docs/REPAIR.md`
* `docs/PRD.md`
* `AGENTS.md`

These may be updated only after implementation and validation to reflect actual status. Do not create `GOAL.md`.

### Out of scope

Explicitly out of scope:

* unrelated formatting-only changes;
* broad package renames;
* broad architecture rewrites;
* Forge/Gradle/Java/Baubles upgrades;
* moving to modern Forge/Fabric;
* changing `settings.gradle`, Gradle wrapper, Dockerfile or dependency versions unless validation is impossible and the blocker is documented first;
* changing public `thaumcraft.api.*` signatures without a compatibility-preserving adapter;
* changing mod id, registry names, config keys, env vars or resource paths unless parity requires adding missing entries;
* database/schema work: no database is present or required;
* generated/vendor/lock files: none should be introduced;
* modifying `thaumcraft_src/**` or the original JAR;
* touching unrelated `src/main/java/thaumcraft/common/**` modules not involved in a documented blocker;
* changing authentication/authorization: not applicable;
* changing external integrations beyond existing Baubles/optional compatibility boundaries.

## 7. Allowed Changes

Allowed changes for the future `/goal` run:

* Implement missing server behavior in documented focus classes.
* Add small internal helpers for repeated wand/vis/bauble/focus logic if duplication would otherwise appear in multiple focus classes.
* Complete Arcane Bore server mining loop if it is accepted as a blocker rather than deferred.
* Implement bauble vis storage/consumption and runic behavior using existing Baubles integration.
* Implement thaumometer scan server action and research state updates using existing capability/research managers.
* Add `.thaum`/`.thaumbak` compatibility loading if required for original save parity.
* Fix Frugal/focus enchant applicability without changing unrelated enchant behavior.
* Complete boss/special mob TODOs using original 1.7.10 behavior as reference.
* Fix equipment/armor repairability using existing `IRepairable`/`IRepairableExtended` conventions.
* Add missing client GUI/render/TESR/entity render/model/particle/shader classes under `thaumcraft.client.*`.
* Add missing resource files for models/blockstates/recipes/lang/sounds/shaders under existing asset paths.
* Add or update focused tests only if practical; if Forge test infra is absent, add lightweight internal regression checks only where project conventions allow.
* Update docs status after implementation and validation.
* Remove dead stubs after all callers have migrated and compile/build confirms they are unused.
* Adjust internal imports and package-private helpers within stated scope.

## 8. Forbidden Changes

Forbidden changes:

* Do not create `GOAL.md`.
* Do not modify `thaumcraft_src/**` or `Thaumcraft-1.7.10-4.2.3.5.jar`.
* Do not change `thaumcraft.api.*` public signatures unless unavoidable for Forge 1.12.2 compatibility; if unavoidable, preserve source/binary compatibility where practical and document the exact reason.
* Do not rename packages away from `thaumcraft.common.*`, `thaumcraft.client.*`, `thaumcraft.api.*`, `thaumcraft.codechicken.*`.
* Do not change mod id, registry names, item/block/entity names, dimension ids, GUI ids, packet ids or NBT keys silently.
* Do not change config key names/default semantics silently.
* Do not upgrade Forge, Gradle, Java, Baubles or bundled CodeChicken code for convenience.
* Do not introduce new dependencies unless the parity feature cannot be implemented otherwise and the final report justifies it.
* Do not replace existing capability/NBT persistence with incompatible storage.
* Do not rewrite the rendering pipeline beyond documented 1.12.2 adaptation.
* Do not remove existing assets/resources unless they are proven obsolete and replaced.
* Do not perform unrelated formatting, import sorting or cleanup outside touched files.
* Do not suppress build/runtime errors without investigation.
* Do not continue into Phase 8 if pre-Phase8 P0 blockers remain unresolved or not explicitly deferred with evidence.
* Do not update docs to claim parity unless validation supports it.

## 9. Acceptance Criteria

### Functional Acceptance Criteria

* [ ] Existing behavior that already works in Phase 0-7 remains preserved.
* [ ] All documented P0 blockers in `docs/REPAIR.md` are either implemented or explicitly accepted as deferred risk with evidence.
* [ ] Six documented focus server actions perform original-equivalent behavior where Forge 1.12.2 API allows it.
* [ ] Wand vis consumption, discounts and bauble vis storage/usage match original intent and existing centi-vis conventions.
* [ ] Thaumometer scan action updates player knowledge/capabilities correctly and remains side-safe.
* [ ] Arcane Bore either performs original-equivalent block scanning/digging/mining server loop or is documented as a conscious blocker/defer with reason.
* [ ] Boss/special mob TODO behavior is implemented for server-visible parity.
* [ ] Equipment/armor repairability no longer returns incorrect placeholder `false` values where original behavior allows repair.
* [ ] Phase 8 GUI/rendering/FX/shader flows work for critical user flows: open core GUIs, view key tile entities, spawn/render mobs/projectiles, use core particle effects.
* [ ] Phase 9 recipes/research entries are registered, unlockable and reference valid recipe/registry names.
* [ ] Public contracts remain compatible: API jar shape, registry names, config keys, NBT keys, packet contracts.
* [ ] Edge cases directly related to changed systems are covered by focused checks or documented manual smoke scenarios.

### Architecture Acceptance Criteria

* [ ] Server gameplay logic remains in `thaumcraft.common.*`, not client classes.
* [ ] Client rendering/GUI/FX logic remains in `thaumcraft.client.*`, not common classes.
* [ ] Shared wand/vis/bauble/focus logic is centralized only where repeated by multiple callers.
* [ ] No speculative abstractions are introduced.
* [ ] Old placeholder/no-op implementations are removed after real behavior is connected.
* [ ] New module boundaries are used by all intended callers.
* [ ] Public `thaumcraft.api.*` remains stable.
* [ ] Original package/field/method naming conventions are preserved for traceability.
* [ ] Reference behavior is checked against `thaumcraft_src/**` or CFR decompilation for every gameplay-critical touched class.

### Test Acceptance Criteria

* [ ] Existing Gradle `test` task is run if available, even if no tests are present.
* [ ] Existing relevant tests, if any are found locally, pass.
* [ ] New or updated tests are added only if project conventions and Forge test setup make them practical.
* [ ] Where automated tests are not practical, focused manual regression scenarios are documented in the final report.
* [ ] Regression coverage/checks exist for high-risk changed behavior: foci, wand/bauble vis consumption, research state, Arcane Bore, boss behavior, recipes/research registration, GUI opening/render smoke paths.

### Validation Acceptance Criteria

* [ ] Required validation commands listed in section 10 are run where practical.
* [ ] `compileJava` passes after each significant checkpoint.
* [ ] `build` passes before successful stop.
* [ ] `apiJar` and `devJar` build successfully before successful stop.
* [ ] `runClient` is executed if graphics/X11 environment is available; otherwise skipped with explicit environment reason.
* [ ] `runServer` is executed if ForgeGradle task/environment supports it; otherwise skipped with explicit reason.
* [ ] Any failing command is investigated.
* [ ] Pre-existing failures are documented with exact command, failure excerpt and evidence that failure predates the change.
* [ ] Final report includes exact commands and results.

### Diff Acceptance Criteria

* [ ] Diff stays inside stated scope.
* [ ] No unrelated files are changed.
* [ ] No secrets, local absolute paths or machine-specific values are introduced.
* [ ] No generated build output is committed.
* [ ] No vendored/reference source is modified.
* [ ] Final diff is reviewable module-by-module: server/common blockers, client/rendering, recipes/research, docs/status.

## 10. Required Validation Commands

Use Docker because repository docs require Java 8 and provide a Docker development environment.

### Install/setup check

Build the development image:

```bash
docker build -t thaumcraft-dev .
```

Notes:

* Expensive on first run.
* Requires network access for base image/packages/CFR download.
* Uses Docker.

Verify Gradle task discovery:

```bash
docker run --rm \
  -v "$(pwd):/workspace/thaumcraft" \
  -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
  --user 1000:1000 \
  --entrypoint ./gradlew \
  thaumcraft-dev tasks
```

Run Forge setup if the workspace/cache is not initialized:

```bash
docker run --rm \
  -v "$(pwd):/workspace/thaumcraft" \
  -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
  --user 1000:1000 \
  --entrypoint ./gradlew \
  thaumcraft-dev setupDecompWorkspace
```

Notes:

* Expensive.
* Requires network access for Gradle/Forge/MCP/CurseMaven dependencies unless already cached.
* Required before IDE/runtime tasks on a fresh checkout.

### Unit tests

Run Gradle test task if available:

```bash
docker run --rm \
  -v "$(pwd):/workspace/thaumcraft" \
  -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
  --user 1000:1000 \
  --entrypoint ./gradlew \
  thaumcraft-dev test
```

Notes:

* No dedicated test suite was found in inspected docs.
* If this task is no-op because no tests exist, document that.

### Integration tests / runtime smoke checks

Run client smoke check:

```bash
docker run --rm -it \
  -v "$(pwd):/workspace/thaumcraft" \
  -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
  -e DISPLAY="$DISPLAY" \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  --user 1000:1000 \
  --entrypoint ./gradlew \
  thaumcraft-dev runClient
```

Notes:

* Expensive.
* Requires graphics/X11 or equivalent display setup.
* Requires cached/downloadable Forge runtime dependencies.
* Use to verify clean world load, creative tab browse, GUI opening, key render paths, key item interactions.

Run server smoke check if task is available:

```bash
docker run --rm \
  -v "$(pwd):/workspace/thaumcraft" \
  -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
  --user 1000:1000 \
  --entrypoint ./gradlew \
  thaumcraft-dev runServer
```

Notes:

* Expensive.
* May require EULA/runtime setup depending on ForgeGradle behavior.
* If task is unavailable or blocked, document exact reason.

### Lint

No repository lint task was found. Do not add a new lint tool as part of this goal.

Use compile/build as the primary Java validation.

### Typecheck / compile

```bash
docker run --rm \
  -v "$(pwd):/workspace/thaumcraft" \
  -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
  --user 1000:1000 \
  --entrypoint ./gradlew \
  thaumcraft-dev compileJava
```

Run after every checkpoint and after each risky migration group.

### Formatting check

No repository formatting check was found. Do not introduce formatter-driven broad changes.

### Build

Process resources:

```bash
docker run --rm \
  -v "$(pwd):/workspace/thaumcraft" \
  -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
  --user 1000:1000 \
  --entrypoint ./gradlew \
  thaumcraft-dev processResources
```

Build API/dev jars:

```bash
docker run --rm \
  -v "$(pwd):/workspace/thaumcraft" \
  -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
  --user 1000:1000 \
  --entrypoint ./gradlew \
  thaumcraft-dev apiJar devJar
```

Full build:

```bash
docker run --rm \
  -v "$(pwd):/workspace/thaumcraft" \
  -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
  --user 1000:1000 \
  --entrypoint ./gradlew \
  thaumcraft-dev build
```

Clean full build before final stop:

```bash
docker run --rm \
  -v "$(pwd):/workspace/thaumcraft" \
  -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
  --user 1000:1000 \
  --entrypoint ./gradlew \
  thaumcraft-dev clean build
```

Notes:

* Expensive.
* Requires network on first run unless `.gradle_home` is warm.

### Project-specific validation

Find remaining documented stubs/placeholders in touched scope:

```bash
docker run --rm \
  -v "$(pwd):/workspace/thaumcraft" \
  thaumcraft-dev \
  -c "rg -n \"TODO|TBD|placeholder|Phase 8:|return false|return null|no-op\" src/main/java/thaumcraft docs/REPAIR.md docs/PRD.md"
```

Notes:

* Do not treat every match as a required change.
* Investigate matches in touched scope and documented blockers.

Decompile original reference class before implementing parity for a touched class:

```bash
docker run --rm \
  -v "$(pwd):/workspace/thaumcraft" \
  thaumcraft-dev \
  -c "cfr thaumcraft_src/thaumcraft/common/items/wands/foci/FocusPortableHole.class"
```

For each gameplay-critical touched class, replace the `.class` path with the corresponding original class path under `thaumcraft_src/**`.

Verify no accidental modifications to reference/vendor/build outputs:

```bash
git status --short
git diff --stat
git diff --name-only
```

Verify public API jar surface did not accidentally lose `thaumcraft/api/**` output:

```bash
docker run --rm \
  -v "$(pwd):/workspace/thaumcraft" \
  -v "$(pwd)/.gradle_home:/home/ubuntu/.gradle" \
  --user 1000:1000 \
  --entrypoint ./gradlew \
  thaumcraft-dev apiJar
```

Then inspect generated API jar locally if needed:

```bash
jar tf build/libs/Thaumcraft-*-api.jar | rg '^thaumcraft/api/'
```

## 11. Implementation Plan

### Checkpoint 1: Baseline and behavior mapping

Goal:

* Understand current behavior and exact gaps before code changes.
* Map call graph/data flow for each documented blocker.
* Establish validation baseline.

Expected files touched:

* None initially.
* Optional docs notes only if absolutely necessary; prefer final report over docs during baseline.

Actions:

* Read `AGENTS.md`, `docs/PRD.md`, `docs/REPAIR.md`, `build.gradle`, `gradle.properties`, `settings.gradle`.
* Verify local file tree for missing README/CONTRIBUTING/CI/test config.
* Run `git status --short`.
* Run `compileJava`.
* Run `build` if baseline compile passes and time/resources allow.
* Use `rg` to locate TODO/stub/no-op matches in documented scope.
* For every targeted class, decompile/read original counterpart from `thaumcraft_src/**` before implementation.

Validation:

* `compileJava`
* `test` if task exists
* `build` if practical
* `rg` placeholder scan

Rollback/safety notes:

* If baseline validation fails before edits, document exact failure and do not “fix around it” unless it blocks all work and is in scope.
* If repo state is dirty before work, stop or document exact pre-existing changes.

### Checkpoint 2: Introduce target structure

Goal:

* Add only the minimal internal structure needed to implement missing parity safely.
* Keep old behavior connected until replacements are complete.

Expected files that may be touched:

* `WandManager.java`
* `ItemWandCasting.java`
* relevant `Focus*.java`
* relevant common helpers under `thaumcraft.common.lib/**`
* client registration scaffolding under `thaumcraft.client/**` only when entering Phase 8
* recipe/research registration scaffolding only when entering Phase 9.

Allowed structure additions:

* small helper for side-safe focus execution;
* small helper for wand/vis/bauble accounting;
* small helper for original-compatible research/offline data loading;
* client render registration helpers if existing `ClientProxy` would otherwise become unmaintainable;
* recipe/research registration helpers if they mirror existing project conventions.

Validation:

* `compileJava`
* focused `rg` for accidental TODO/no-op expansion
* `apiJar` if any API-adjacent file was touched

Rollback/safety notes:

* Do not introduce abstractions that are not immediately used by at least two concrete callers.
* Do not move public API signatures.
* If a helper starts to require dependency/version changes, stop and reassess.

### Checkpoint 3: Migrate callers incrementally

Goal:

* Implement and connect missing behavior in small, reviewable groups.
* Run focused validation after each group.

Migration groups:

1. Remaining focus server actions:

   * `FocusPortableHole`
   * `FocusTrade`
   * `FocusPech`
   * `FocusHellbat`
   * `FocusExcavation`
   * `FocusWarding`
   * related `WandManager`/vis helpers.

2. Arcane Bore and major server-visible tile behavior:

   * `TileArcaneBore`
   * related block/item/helper paths only if necessary.

3. Baubles, relics and wand integration:

   * `ItemRingRunic`
   * `ItemAmuletVis`
   * `ItemThaumometer`
   * `WandManager`
   * player capability/research update paths.

4. Research compatibility and enchantments:

   * `ResearchManager`
   * `PlayerKnowledge`
   * missing research helper classes if documented by original parity
   * `EnchantmentFrugal`.

5. Boss/special mobs:

   * `EntityCultistLeader`
   * `EntityEldritchGolem`
   * `EntityEldritchWarden`
   * `EntityInhabitedZombie`
   * `EntityPech`.

6. Tools/armor repairability:

   * `ItemPrimalCrusher`
   * `src/main/java/thaumcraft/common/items/equipment/**`
   * `src/main/java/thaumcraft/common/items/armor/**`.

7. Phase 8 client:

   * GUIs first, because they validate containers/user flows.
   * TESR/entity renderers next.
   * models/resources next.
   * particles/beams/shaders last.

8. Phase 9 recipes/research:

   * vanilla JSON recipes;
   * arcane/infusion/crucible recipe registration;
   * research entries/pages;
   * aspect/entity tags and smelting bonuses.

9. Phase 10 polish:

   * sound/config/compat/performance only where necessary for parity/stability.

Validation after each group:

* `compileJava`
* `processResources` after resource changes
* `apiJar` after API-adjacent changes
* `runClient` after client GUI/rendering/resource milestones if environment permits
* focused manual scenario notes for changed gameplay.

Rollback/safety notes:

* If a group expands beyond documented scope, pause and document blocker.
* Preserve compatibility until all intended callers are migrated.
* Avoid mixing server gameplay, client rendering and recipe-content changes in one unreviewable diff.

### Checkpoint 4: Remove old implementation

Goal:

* Remove obsolete placeholders and dead code only after replacements are connected.

Expected files that may be touched:

* Same files touched in Checkpoint 3.
* Docs status files if implementation is complete.

Actions:

* Remove no-op comments like “Phase 8: ...” only when real behavior exists.
* Remove unused imports/types.
* Remove obsolete stubs only when no callers depend on them.
* Update `docs/REPAIR.md`, `docs/PRD.md` and/or `AGENTS.md` status only after validation.

Validation:

* `compileJava`
* `build`
* `rg` placeholder scan in touched scope
* `git diff --name-only`
* `git diff --stat`

Rollback/safety notes:

* Do not delete tests/docs/resources unless replaced by better coverage or correct resources.
* Do not remove reference material.
* If deleting a stub would change public API, keep compatibility shim.

### Checkpoint 5: Final validation and report

Goal:

* Prove the final repository state is coherent, bounded and reviewable.

Validation:

* `git status --short`
* `git diff --stat`
* `git diff --name-only`
* `compileJava`
* `test`
* `processResources`
* `apiJar devJar`
* `build`
* `clean build` if practical
* `runClient` if display environment is available
* `runServer` if available/practical
* project-specific `rg` placeholder scan

Final checks:

* No unrelated files changed.
* No generated build output committed.
* No `GOAL.md` created.
* No reference source modified.
* No public API break unless explicitly documented.
* Final report printed with exact commands/results.

Rollback/safety notes:

* If full validation fails for unclear reasons, do not claim success.
* If runtime smoke cannot run due to environment, document why and provide exact command.

## 12. Safety Rules for the Future `/goal` Run

* Preserve behavior unless explicitly allowed by this manifest.
* Prefer small reversible changes.
* Do not perform unrelated cleanup.
* Do not introduce new dependencies unless necessary and justified.
* Do not silently change public contracts.
* Do not ignore failing tests or validation commands.
* Do not continue through major ambiguity; document a blocker.
* Keep the repository in a coherent state after each checkpoint.
* Prefer existing project conventions over new patterns.
* Always decompile/read original reference behavior before implementing parity for a gameplay-critical class.
* Keep original package names and field/method names where practical.
* Avoid ad-hoc regex fixes for broken Java signatures.
* Do not edit `thaumcraft_src/**`.
* Do not create `GOAL.md`.
* Do not use docs updates as a substitute for real implementation.
* Do not mix large unrelated server/client/content changes in one step.
* Treat absence of automated tests as higher risk, not as permission to skip validation.

## 13. Stop Conditions

Successful stop:

* all acceptance criteria are satisfied;
* required validation commands pass, or pre-existing/environment failures are documented with evidence;
* final diff stays inside scope;
* no public contracts are silently changed;
* final report is complete;
* repository is left in a coherent buildable state.

Blocker stop:

* required original behavior is ambiguous and cannot be safely inferred from `thaumcraft_src/**`, docs or existing code;
* implementation requires out-of-scope API/schema/product changes;
* validation cannot run due to missing Docker/network/JDK/Forge/graphics environment;
* tests/build fail for unclear reasons;
* a public API, registry, NBT or config compatibility break appears necessary;
* repository state is dirty or unsafe before edits;
* completing the requested parity would require dependency/version upgrades not allowed by this manifest;
* Phase 8 work is reached while P0 server blockers are neither fixed nor explicitly deferred.

## 14. Final Report Template

The future `/goal` run must print the final report in this exact structure:

```markdown
# Final Report: Thaumcraft 4.2.3.5 -> Forge 1.12.2 Parity Work

## 1. Summary of changes

- <concise summary of what was implemented>
- <explicit statement of whether full requested scope was completed or a blocker was hit>

## 2. Files modified

- `<path>` — <why it changed>
- `<path>` — <why it changed>

## 3. Architecture before/after

Before:
- <old responsibility/data flow/stub state>

After:
- <new responsibility/data flow/implemented state>

Public contracts:
- API compatibility: <preserved/changed with reason>
- Registry names: <preserved/changed with reason>
- NBT/config/data compatibility: <preserved/changed with reason>

## 4. Acceptance criteria checklist

### Functional Acceptance Criteria

- [ ] <criterion> — <pass/fail/evidence>

### Architecture Acceptance Criteria

- [ ] <criterion> — <pass/fail/evidence>

### Test Acceptance Criteria

- [ ] <criterion> — <pass/fail/evidence>

### Validation Acceptance Criteria

- [ ] <criterion> — <pass/fail/evidence>

### Diff Acceptance Criteria

- [ ] <criterion> — <pass/fail/evidence>

## 5. Validation commands and results

- Command: `<exact command>`
  - Result: `<passed/failed/skipped>`
  - Evidence: `<short output excerpt or reason>`

- Command: `<exact command>`
  - Result: `<passed/failed/skipped>`
  - Evidence: `<short output excerpt or reason>`

## 6. Known limitations

- <limitation or “None known”>

## 7. Blockers, if any

- <blocker>
- Required next decision: <decision>

## 8. Suggested follow-up tasks

- <small bounded task>
- <small bounded task>
```

## 15. Ready-to-Run `/goal` Command

```text
/goal Read the GOAL manifest from this conversation first and complete the refactor exactly as specified. Work checkpoint by checkpoint, preserve existing behavior, keep the diff focused, run the required validation commands after each checkpoint where practical, and stop only when every acceptance criterion is satisfied or a blocker is documented in the final report.
```

# Summary

1. Выбранный scope: documented parity completion for the existing Forge 1.12.2 port — сначала pre-Phase8 server blockers, затем Phase 8 client GUI/rendering/FX/shaders, затем Phase 9 recipes/research/content, затем limited Phase 10 polish. Unrelated cleanup, dependency upgrades and package rewrites excluded.

2. Найденные validation commands: Docker image build, `./gradlew tasks`, `setupDecompWorkspace`, `compileJava`, `test`, `processResources`, `apiJar devJar`, `build`, `clean build`, optional `runClient`, optional `runServer`, `rg` placeholder scan, `cfr` original-class decompilation, `git status/diff` checks.

3. Главные риски: missing automated tests, large client rendering scope, NBT/save compatibility, Forge registry/resource identity, wand/vis/bauble accounting, client/server side separation, original 1.7.10 behavior ambiguity, performance-sensitive systems such as vis network/infusion/particles.

4. Assumptions to verify before `/goal`: malformed user URL is intended to be `0FL01/Thaumcraft-4.2-FOREVA`; no hidden CI/test config exists; Docker image can be built locally; Forge/CurseMaven dependencies are reachable or cached; `compileJava` still passes on current `master`; runtime smoke checks have a usable client/server environment.

[1]: https://github.com/0FL01/Thaumcraft-4.2-FOREVA "GitHub - 0FL01/Thaumcraft-4.2-FOREVA: Full port Thaumcraft 4.2 on minecraft 1.12.2 (WIP) · GitHub"
[2]: https://github.com/0FL01/Thaumcraft-4.2-FOREVA/raw/refs/heads/master/build.gradle "raw.githubusercontent.com"
[3]: https://github.com/0FL01/Thaumcraft-4.2-FOREVA/raw/refs/heads/master/AGENTS.md "raw.githubusercontent.com"
[4]: https://github.com/0FL01/Thaumcraft-4.2-FOREVA/blob/master/docs/PRD.md "Thaumcraft-4.2-FOREVA/docs/PRD.md at master · 0FL01/Thaumcraft-4.2-FOREVA · GitHub"
[5]: https://github.com/0FL01/Thaumcraft-4.2-FOREVA/blob/master/docs/REPAIR.md "Thaumcraft-4.2-FOREVA/docs/REPAIR.md at master · 0FL01/Thaumcraft-4.2-FOREVA · GitHub"

