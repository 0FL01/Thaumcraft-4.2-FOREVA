# Stage 6 — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 6 покрывает серверно-видимое поведение сущностей Thaumcraft 4.2.3.5: мобы, AI, снаряды, боссы, големы, контейнеры сущностей, дропы, звуки, NBT/data watcher/data manager, spawn rules, entity registration и игровые сценарии взаимодействия. Клиентские рендереры, модели, частицы и визуальные FX остаются Phase 8 dependency, но Stage 6 не может считаться закрытой, если серверная логика только заглушена или не достигается в игре.

По PRD Stage 6 не закрыта: `docs/PRD.md:317-339` задает цель, baseline и acceptance, а `docs/PRD.md:325-333` прямо перечисляет незакрытые риски: boss special attacks, Pech trade/taming/pickup/combat, golem AI/interactions, drops/sounds runtime scenarios.

### Current backend interpretation note

Stage 6 has many entity/golem/mob implementations and static guard checkpoints, but it must not be marked complete from class presence, registration, or source-shape tests.

Do not claim Stage 6 complete until representative dedicated-server scenarios pass for golem placement/core behavior/upgrades/inventory/combat/liquid/essentia/save-load, traveling trunk placement/inventory/defense/dimension transfer/save-load, Pech anger/trade/spawn variants, cultist portal reward path, boss special attacks and phase flows, and projectile effects/status behavior where server-visible.

## 2. Scope фазы

- Entity classes: `src/main/java/thaumcraft/common/entities/**` и reference `thaumcraft_src/thaumcraft/common/entities/**`.
- Monsters and bosses: `src/main/java/thaumcraft/common/entities/monster/**`, включая `boss/**` и `mods/**`.
- Projectiles: `src/main/java/thaumcraft/common/entities/projectile/**`.
- Golems and trunks: `src/main/java/thaumcraft/common/entities/golems/**`.
- Entity AI: `src/main/java/thaumcraft/common/entities/ai/**`.
- Entity containers/inventories: `src/main/java/thaumcraft/common/entities/ContainerPech.java`, `src/main/java/thaumcraft/common/entities/InventoryPech.java`, `src/main/java/thaumcraft/common/entities/golems/ContainerGolem.java`, `src/main/java/thaumcraft/common/entities/golems/ContainerTravelingTrunk.java`, `src/main/java/thaumcraft/common/entities/InventoryMob.java`, `src/main/java/thaumcraft/common/entities/golems/InventoryTrunk.java`.
- Registrations/config: `src/main/java/thaumcraft/common/config/ConfigEntities.java`, entity registration event in `src/main/java/thaumcraft/common/Thaumcraft.java:257-259`, config knobs in `src/main/java/thaumcraft/common/config/Config.java:100-110` and `src/main/java/thaumcraft/common/config/Config.java:273-289`.
- Sounds/resources: sound constants in `src/main/java/thaumcraft/common/lib/TCSounds.java:18-83`, registration in `src/main/java/thaumcraft/common/lib/TCSounds.java:93-96`, sound JSON in `src/main/resources/assets/thaumcraft/sounds.json:1-68`.
- Spawn rules and world hooks relevant to Stage 6: magical forest Pech/Wisp spawn entries in `src/main/java/thaumcraft/common/lib/world/biomes/BiomeMagicalForest.java:44-48`; boss/minion spawning in `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortal.java:197-229`; Inhabited Zombie crab spawn in `src/main/java/thaumcraft/common/entities/monster/EntityInhabitedZombie.java:17-43`.
- Dependencies: content/item registration and BlockLoot are outside pure entity logic, but directly block Stage 6 golem and Cultist Portal scenarios.

Scenarios that should work before closure:

- All Stage 6 entities register with stable registry names and spawn without server crash.
- Pech spawn with correct type/equipment, value/taming logic, pickup, trade container outputs, anger/group retaliation, ranged and melee combat, death loot, and sounds.
- Golems can be obtained/placed, assigned cores/upgrades/decorations, linked with bell/markers, open server containers, execute all core AI flows, persist NBT, carry items/fluids/essentia, fight, drop carried inventory, and use sounds.
- Bosses and special mobs execute server attacks, phase transitions, minion spawning, field/block behavior, drops, sounds, NBT persistence, and target/team rules.
- Projectiles apply reference damage/effects, lifetime, gravity, homing/redirect behavior, impact sounds/status events, and spawn data.
- Drops and sounds are verified in runtime scenarios; loot-table JSON is not required if original behavior is code-driven, but code-driven drops must match reference.

## 3. Источники сравнения

- Product requirements: `docs/PRD.md:317-339`.
- Build/runtime constraints: `build.gradle:24-29`, `Dockerfile:61-69`, `AGENTS.md` repository instructions.
- Current implementation root: `src/main/java/thaumcraft/common/entities/**`.
- Current entity registration: `src/main/java/thaumcraft/common/config/ConfigEntities.java:51-152` and `src/main/java/thaumcraft/common/Thaumcraft.java:257-259`.
- Current sounds: `src/main/java/thaumcraft/common/lib/TCSounds.java:18-96`, `src/main/resources/assets/thaumcraft/sounds.json:1-68`.
- Reference classes: `thaumcraft_src/thaumcraft/common/entities/**`.
- Targeted CFR decompile was used for concrete behavior comparison from `thaumcraft_src/thaumcraft/common/entities/monster/EntityPech.class`, `thaumcraft_src/thaumcraft/common/entities/golems/EntityGolemBase.class`, `thaumcraft_src/thaumcraft/common/entities/monster/boss/EntityCultistLeader.class`, `thaumcraft_src/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.class`, `thaumcraft_src/thaumcraft/common/entities/monster/boss/EntityEldritchWarden.class`, `thaumcraft_src/thaumcraft/common/entities/monster/EntityInhabitedZombie.class`, `thaumcraft_src/thaumcraft/common/entities/projectile/EntityGolemOrb.class`, `thaumcraft_src/thaumcraft/common/entities/projectile/EntityEldritchOrb.class`, and `thaumcraft_src/thaumcraft/common/entities/projectile/EntityPechBlast.class`.
- Lightweight commands run for analysis: `git status --short`; `comm -3 <reference entity classes> <current entity java classes>`; `cfr --silent true <targeted Stage 6 classes>`; static grep/glob/read inspection. No build or runtime smoke was run.

## 4. Текущее состояние Stage 6

Current implementation has broad class coverage: a class-name comparison between non-inner reference `.class` files under `thaumcraft_src/thaumcraft/common/entities` and current `.java` files under `src/main/java/thaumcraft/common/entities` found only one absent current top-level entity-support class: `thaumcraft/common/entities/ItemSpawnerEgg`. Core entity classes, boss classes, projectile classes, AI classes, champion modifier classes, golem classes, containers, and inventories mostly exist.

Entity registrations exist in `src/main/java/thaumcraft/common/config/ConfigEntities.java:55-129`: base entities, projectiles, golems, zombies, wisps/bats, Pech, eldritch mobs, cultists, bosses, thaumic slime, taint mobs, and item grate are registered. Forge registration is wired through `src/main/java/thaumcraft/common/Thaumcraft.java:257-259`.

However, class presence is not parity. Several server-visible flows are absent or partially ported:

- Golem item classes exist but are not registered in `ConfigItems` and are mostly shells, so normal golem/trunk placement and golem bell workflows are blocked.
- Pech trading UI/container exists, but trade output generation from the original `tradeInventory` table is absent in current `InventoryPech`.
- Pech spawn equipment/type initialization and group anger behavior are incomplete compared with reference.
- Cultist Portal uses vanilla chests as a placeholder because `BlockLoot` is absent, changing server-visible boss reward behavior.
- Projectiles implement core damage/effects, but impact sounds/status events and some reference side effects are missing or reduced.
- Boss special attacks have implementation baselines, but no runtime/manual validation is documented; PRD explicitly says Stage 6 is not closed on build-only evidence.
- Several sounds return `null` for mobs in current code, and Stage 6 drop/sound scenarios have not been runtime-verified.

Client-only renderer/particle TODOs are Phase 8 dependencies. They are not counted as Stage 6 blockers unless they replace server-visible sounds, packets, status events, drops, or gameplay effects.

## 5. Gap list

### GAP-1: Golem and trunk item registration/placement is blocked

**Статус:** отсутствует / частично реализовано  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigItems.java:45-181`
- `src/main/java/thaumcraft/common/entities/golems/ItemGolemPlacer.java:9-15`
- `src/main/java/thaumcraft/common/entities/golems/ItemTrunkSpawner.java:9-15`
- `src/main/java/thaumcraft/common/entities/golems/ItemGolemCore.java:9-25`
- `src/main/java/thaumcraft/common/entities/golems/ItemGolemUpgrade.java:9-15`
- `src/main/java/thaumcraft/common/entities/golems/ItemGolemDecoration.java:9-15`
- `src/main/java/thaumcraft/common/entities/golems/ItemGolemBell.java:16-42`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/golems/ItemGolemPlacer.class`
- `thaumcraft_src/thaumcraft/common/entities/golems/ItemTrunkSpawner.class`
- `thaumcraft_src/thaumcraft/common/entities/golems/ItemGolemCore.class`
- `thaumcraft_src/thaumcraft/common/entities/golems/ItemGolemUpgrade.class`
- `thaumcraft_src/thaumcraft/common/entities/golems/ItemGolemDecoration.class`
- `thaumcraft_src/thaumcraft/common/entities/golems/ItemGolemBell.class`
- `thaumcraft_src/thaumcraft/common/entities/golems/EntityGolemBase.class`

**Что не совпадает:**
Checkpoints 8.2.15 and 8.2.16 add `ConfigItems` fields/registrations for `itemGolemBell`, `itemGolemCore`, `itemGolemPlacer`, `itemGolemUpgrade`, `itemGolemDecoration`, and `itemTrunkSpawner`, restore the original metadata/subitem surface, and port the server-side golem/trunk item spawn paths. Checkpoint 8.2.17 restores bell link/marker editing, marker side/color identity, decoration application, wheat healing, and upgrade inventory refresh. Checkpoint 8.2.18 restores bell left-click pickup/packing behavior for golems and trunks. Checkpoint 8.2.19 restores traveling trunk baseline stats, upgrade application, feeding, stay-aware following, pickup upgrade pull behavior, and death inventory drops. Checkpoint 8.2.22 restores upgrade `2` owner-target defense, and checkpoint 8.2.23 restores linked cross-dimension owner-follow transfer. Remaining workflow gaps are runtime/manual placement and transfer evidence.

**Что нужно доделать:**
Register the golem/trunk items with stable Thaumcraft names and port their server behavior so players can obtain, place, configure, and link golems/trunks.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Add fields and registration entries in `src/main/java/thaumcraft/common/config/ConfigItems.java` for golem bell, core, placer, upgrade, decoration, and trunk spawner.
- Port `ItemGolemPlacer` placement: create `EntityGolemBase`, set type/advanced/owner/home/facing/upgrades, call setup, consume stack, play sound/status.
- Port `ItemTrunkSpawner` placement for `EntityTravelingTrunk`.
- Port metadata/subitem behavior for cores/upgrades/decorations.
- Ensure existing `EntityGolemBase.processInteract` checks use registered ConfigItems or equivalent stable item references.
- Add item model/lang resources only as dependency if required for runtime use; renderer visuals remain Phase 8.
- Scenario: in a server world, use each placer/core/upgrade/bell path and confirm entities spawn and persist.

**Критерии приемки:**
- [ ] Golem/trunk items are registered and visible/obtainable through expected item ids.
- [ ] A golem and traveling trunk can be placed on a dedicated server without crash.
- [ ] Core, upgrade, decoration, and bell workflows reach `EntityGolemBase` server logic and persist through save/load.

**Риски / зависимости:**
Depends on item/content registration outside pure entity classes. This is a direct Stage 6 blocker because golem entity behavior cannot be exercised normally without these items.

### GAP-2: Golem base interaction/NBT/server behavior is only partially ported

**Статус:** server-side baseline implemented; runtime evidence open
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/golems/EntityGolemBase.java:90-131`
- `src/main/java/thaumcraft/common/entities/golems/EntityGolemBase.java:133-239`
- `src/main/java/thaumcraft/common/entities/golems/EntityGolemBase.java:241-314`
- `src/main/java/thaumcraft/common/entities/golems/EntityGolemBase.java:316-354`
- `src/main/java/thaumcraft/common/entities/golems/EntityGolemBase.java:586-629`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/golems/EntityGolemBase.class`

**Что не совпадает:**
Reference behavior includes inactive state from the pedestal/cosmetic block under the golem, bootup sounds/status, fire resistance override, armor calculation, material-dependent water pathing, no-drowning air handling, melee enchantment callbacks, upgrade retaliation, target range validation, animal/butcher target exclusions, death logging, bell/deco/wheat interactions, fluid-carried NBT for fluid cores, GUI blocking while holding wand, setup inventory after upgrades, spawn data reconstruction, and item/bell constants through `ConfigItems`. Checkpoints 8.2.17, 8.2.20, 8.2.21, 8.2.24, 8.2.25, 8.2.26, 8.2.27, 8.2.28, 8.2.29, 8.2.30, 8.2.31, 8.2.32, and 8.2.33 restore the main bell/deco/wheat interaction branch, held-wand GUI exclusion, upgrade inventory refresh, fluid-carried/toggle NBT persistence, carried item sync after reload, ranged golem shot sound, the golem-stone inactive state, carried item/fluid/essentia display sync, server-side death logging, fire-resistance guards, reference armor calculation, material-dependent water pathing, no-drowning air handling, melee enchantment callbacks/knockback, upgrade retaliation, target range validation, and animal/butcher target exclusions. Current remaining static gaps include bootup client sound parity and runtime/manual evidence for the full core matrix.

**Что нужно доделать:**
Port the missing server-visible golem lifecycle and interaction details from reference, without moving renderer/FX work into Stage 6.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Inactive golem-stone behavior is restored; verify it in runtime placement scenarios.
- Core 5 fluid-carried NBT, toggle NBT, and ranged shot sound are restored; verify them in runtime save/load/combat scenarios.
- Decoration application/removal, wheat healing/speed behavior, bell interaction behavior, held-wand GUI exclusion, and upgrade inventory refresh are restored; verify them in runtime scenarios.
- Server-side death logging is restored; verify the log line during a runtime golem death scenario.
- Fire-resistant golems now reject fire damage and ignition; verify clay/stone/iron/thaumium fire scenarios in runtime.
- Golem armor now includes type armor plus visor/plate decoration bonuses; verify damage reduction in runtime combat.
- Stone, iron, and thaumium golems now get the non-avoiding water pathing adaptation; verify water traversal in runtime.
- Golems now preserve air supply while submerged; verify drowning immunity in runtime.
- Golem melee now applies reference enchantment damage, knockback, fire, thorns, and arthropod callbacks; verify combat in runtime.
- Upgrade `5` now retaliates against direct attackers with reference thorns damage and sound; verify in runtime combat.
- Golem target validation now rejects entities outside the home/range check; verify target drop behavior in runtime combat.
- Animal-targeting golems now reject tamed animals and golems; butcher core also rejects child animals. Verify animal/butcher target selection in runtime combat.
- Runtime scenarios: gather, empty, pickup, harvest, attack, fluid, essentia, lumber, use, butcher, sort, fish.

**Критерии приемки:**
- [ ] Golem NBT round-trip preserves core, type, advanced, colors, upgrades, markers, carried item, carried fluid, carried essentia, owner, home, and inventory.
- [ ] Golem interaction flows match reference for core/upgrades/decorations/bell/healing/GUI access.
- [ ] All golem core AI scenarios can be manually exercised on a dedicated server without stuck task loops or item loss.

**Риски / зависимости:**
Depends on GAP-1 item registration. Some visual status bytes are Phase 8, but server state changes and sounds are Stage 6.

### GAP-3: Golem AI/helper flows need parity verification and targeted fixes

**Статус:** static mapping documented; runtime registry smoke open
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/ai/**/*.java`
- `src/main/java/thaumcraft/common/entities/golems/GolemHelper.java`
- `src/main/java/thaumcraft/common/entities/golems/EntityGolemBase.java:147-219`
- `src/main/java/thaumcraft/common/config/Config.java:100-110`
- `src/main/java/thaumcraft/common/config/Config.java:273-289`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/ai/**/*.class`
- `thaumcraft_src/thaumcraft/common/entities/golems/GolemHelper.class`
- `thaumcraft_src/thaumcraft/common/entities/golems/EntityGolemBase.class`

**Что не совпадает:**
Current AI classes exist and `EntityGolemBase.setupGolem()` wires all reference core categories (`EntityGolemBase.java:152-213`), but there is no runtime evidence for inventory/fluid/essentia interactions. Helper logic depends on block/tile APIs and inventory semantics that changed between 1.7.10 and 1.12.2. Checkpoint 8.2.34 restores butcher target acquisition from the original AI: the task sorts candidates by age and only starts when more than two valid same-type targets are nearby. Checkpoint 8.2.35 restores golem item-pickup delay handling so player-thrown items are skipped only while their pickup delay is still high instead of being ignored forever. Checkpoint 8.2.36 restores the reference nearest-candidate selection and void-jar penalty for essentia emptying destinations. Checkpoint 8.2.37 restores liquid-core missing-fluid discovery to query the home-adjacent target tank instead of marked source tanks. Remaining null-return paths in `GolemHelper` and AI classes may be valid search failures, but they have not been proven equivalent under real containers, Forge capabilities, fluids, and tile entities.

**Что нужно доделать:**
Run focused manual/server validation per golem core and fix any API mismatch found in AI/helper methods.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Audit `GolemHelper` methods used by each core against reference method intent.
- Validate chest insertion/extraction, color filters, marker range, ore dictionary/NBT/damage toggles, Forge fluid handling, essentia containers, crop/log harvesting, fishing, and combat target selection.
- Butcher target acquisition is restored; verify culling threshold and oldest-target selection in runtime.
- Item pickup delay handling is restored; verify recently thrown items become eligible after the reference delay window.
- Essentia jar destination selection is restored; verify nearest non-void/void target choice in runtime.
- Liquid target-tank discovery is restored; verify fluid empty/gather loops against home-adjacent target tanks in runtime.
- Fix task priority or helper API mismatches in the specific AI classes that fail scenarios.

**Критерии приемки:**
- [ ] Each golem core completes at least one representative server scenario.
- [ ] Failed helper searches are distinguishable from broken 1.12 API ports.
- [ ] Golem config values from `Config.java` affect behavior where reference expects them.

**Риски / зависимости:**
Depends on GAP-1 and GAP-2. Some inventories/tiles/essentia containers are owned by earlier phases; if a tile API is missing, label it as dependency and keep the golem-side fix scoped.

### GAP-4: Pech spawn equipment, type setup, names, and combat task switching are incomplete

**Статус:** server-side baseline implemented; runtime evidence open
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/monster/EntityPech.java:50-97`
- `src/main/java/thaumcraft/common/entities/monster/EntityPech.java:183-200`
- `src/main/java/thaumcraft/common/entities/monster/EntityPech.java:233-273`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/monster/EntityPech.class`

**Что не совпадает:**
Reference Pech initializes random held equipment and derives Pech type during initial spawn: wand Pech gets Pech focus/vis, bow Pech becomes type 2, and equipment drop chances are adjusted. The 2026-05-15 checkpoints restore spawn equipment/type setup, ranged combat-task refresh, and type-specific display names from the original localization keys. Runtime confirmation of natural/command-spawn variants is still open.

**Что нужно доделать:**
Run runtime evidence for reference spawn equipment/type logic, combat AI selection, NBT persistence, and type-specific names.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Server-side implementation is in `EntityPech.onInitialSpawn(...)`, `setCombatTask()`, and `getName()`.
- English type-name keys are in `src/main/resources/assets/thaumcraft/lang/en_us.lang`.
- Runtime scenario: spawn Pechs with each type, verify name/combat variant/NBT persistence.

**Критерии приемки:**
- [ ] Naturally or command-spawned Pechs produce expected melee, archer, and thaumaturge variants.
- [ ] Type 1 Pech fires `EntityPechBlast`; type 2 Pech fires arrows; default Pech uses melee.
- [ ] Pech type persists through NBT and reload.
- [x] Type-specific display-name localization is restored for default, mage, and stalker Pechs.

**Риски / зависимости:**
Depends on current wand/focus items from earlier phases. If wand NBT/focus API is incomplete, document as dependency but keep Pech type/equipment behavior intact.

**Checkpoint 2026-05-15 — Pech spawn variants:**
`EntityPech.onInitialSpawn(...)` now restores the reference random mainhand selection and type derivation. Spawned Pechs can receive a Pech-focus wand with starting primal vis and become type `1`, receive a bow and become type `2`, or receive the reference melee/tool/fishing-rod equipment set and remain the default type. The mainhand drop chance is lowered for wand Pechs, non-wand equipment can receive difficulty-based enchantments, pickup-loot chance follows the reference local-difficulty gate, and `setCombatTask()` is rerun after spawn setup so type `1`/`2` Pechs use ranged AI immediately.

Remaining GAP-4 limits after these checkpoints: held-item change hooks after arbitrary equipment mutation are still limited to existing combat-task calls, and natural/command-spawn runtime coverage remains unvalidated because smoke-server remains environment-blocked and user-driven manual scenarios are excluded.

### GAP-5: Pech anger/group retaliation behavior is incomplete

**Статус:** server-visible baseline improved; runtime evidence open
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/monster/EntityPech.java:224-231`
- `src/main/java/thaumcraft/common/entities/monster/EntityPech.java:206-213`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/monster/EntityPech.class`

**Что не совпадает:**
Reference `attackEntityFrom` makes nearby Pechs angry at the attacking player and sets target/charge behavior for the whole group. The 2026-05-15 checkpoint restores the server-side group anger path, charge status/sound, revenge target assignment, combat-task refresh, and angry target reacquisition while the timer remains active. Runtime confirmation of the group scenario is still open.

**Что нужно доделать:**
Run runtime evidence for group anger, target assignment, charge sound/status, and anger expiry behavior.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Server-side implementation is in `EntityPech.becomeAngryAt(...)`, `attackEntityFrom(...)`, and `onUpdate()`.
- Runtime scenario: attack one Pech near several Pechs and verify group aggro duration and sound.

**Критерии приемки:**
- [x] Server-side path: damaging one Pech with a player angers nearby Pechs in the reference radius.
- [x] Server-side path: angry Pechs keep/reacquire the attacker target until anger expires.
- [ ] Runtime scenario confirms group aggro duration, anger expiry, and charge sound/status.

**Риски / зависимости:**
Client angry particles are Phase 8, but anger target, sounds, and combat are Stage 6.

### GAP-6: Pech trade inventory/output generation is missing

**Статус:** отсутствует / частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/ContainerPech.java:19-88`
- `src/main/java/thaumcraft/common/entities/InventoryPech.java:12-147`
- `src/main/java/thaumcraft/common/entities/monster/EntityPech.java:279-287`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/ContainerPech.class`
- `thaumcraft_src/thaumcraft/common/entities/InventoryPech.class`
- `thaumcraft_src/thaumcraft/common/entities/monster/EntityPech.class`

**Что не совпадает:**
Current Pech container opens only for tamed Pechs (`EntityPech.java:279-287`) and provides one input plus four output slots (`ContainerPech.java:29-33`), but `InventoryPech` never computes output offers when slot 0 changes. Reference Pech has static `tradeInventory` tables by Pech type and value tables for input items; current `EntityPech` has value logic (`EntityPech.java:347-375`) but no trade table/output generation. As written, the GUI can accept items but cannot produce reference trades.

**Что нужно доделать:**
Port Pech trade tables and inventory recalculation semantics.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Add reference trade inventory table initialization in current-safe item ids/metas.
- Implement `InventoryPech.markDirty` or slot-change logic to fill output slots based on Pech type and input value.
- Ensure output slot extraction consumes the correct input stack and plays Pech trade sounds.
- Verify container close drops only unclaimed input/output items correctly.

**Критерии приемки:**
- [ ] Tamed Pech GUI produces non-empty trade offers for valued inputs.
- [ ] Trade outputs match reference type-specific tables where current item equivalents exist.
- [ ] Taking an output consumes the correct input amount and closes without dupes/item loss.

**Риски / зависимости:**
Depends on content/item availability from earlier/later phases. Missing output items should be mapped to current equivalents or documented as content dependency, not silently skipped.

**Checkpoint 2026-05-15 — Pech trade output generation:**
`ContainerPech` now handles the reference trade-roll button path through `enchantItem(..., 0)`: valued input in slot 0 generates up to four output offers, consumes one input item after the roll, can pull one-item offers from the Pech's carried pack, can roll the shared current loot reward path for high-value offers, plays the Pech trade sound on the reference de-tame chance, and tags unclaimed dropped GUI stacks as `PechDrop` so Pechs do not immediately re-collect them. The current-port trade tables are split by Pech type and map original outputs to available 1.12.2 items/blocks where they exist: mana beans, clusters, shards, crystals, knowledge fragments, thaumium tools/boots, focus pouch, Pech focus, vis amulet, runic ring, vanilla apples/XP/blaze/ghast/enchantment books, and custom plants.

Remaining GAP-6 limits after this checkpoint: `GuiPech` is still absent because `ClientProxy` returns `null` for `GUI_PECH`, so player-driven GUI validation remains Phase 8/client work. Original potion metadata and candle outputs do not have direct current-port equivalents in this branch and remain documented content dependencies. Runtime trade interaction, output extraction, and save/reload scenarios are still unvalidated because smoke-server remains environment-blocked and user-driven manual scenarios are excluded.

### GAP-7: Cultist Portal loot crate behavior and reward path still need parity/runtime evidence

**Статус:** частично реализовано / требует проверки
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortal.java:112-150`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortal.java:197-229`
- `src/main/java/thaumcraft/common/blocks/BlockLoot.java`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:197-203`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/monster/boss/EntityCultistPortal.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockLoot.class`

**Что не совпадает:**
Reference portal places Thaumcraft loot blocks/crates during the stage-0 setup. The port now places `ConfigBlocks.blockLootCrate` (with rarity metadata roll) and has `BlockLoot` registered, so the direct vanilla chest placeholder gap is closed. Remaining parity risk is runtime behavior evidence for stage progression, reward drops/state, and full boss-sequence side effects.

**Что нужно доделать:**
Keep the `BlockLoot`/portal reward path parity-proven in static checks and move remaining work to runtime scenario evidence.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Keep `EntityCultistPortal` stage-0 reward placement bound to `ConfigBlocks.blockLootCrate` and prevent regressions back to vanilla chest placement.
- Keep `BlockLoot` registration and block item registration intact in `ConfigBlocks`.
- Verify portal stage setup places banners and loot crates at reference positions and rewards (runtime scenario).

**Критерии приемки:**
- [x] Cultist Portal stage 0 places Thaumcraft loot blocks, not vanilla chest placeholders.
- [ ] Loot crate metadata/state and drops match reference behavior.
- [ ] Portal minion and boss spawn sequence still runs after loot placement.

**Риски / зависимости:**
Runtime proof for reward progression remains a Stage 6 blocker; the direct placeholder/registration gap is closed.

### GAP-8: Eldritch Golem special server behavior is incomplete and unvalidated

**Статус:** server baseline improved; runtime evidence open
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java:27-39`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java:63-84`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java:101-120`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java:128-181`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.class`

**Что не совпадает:**
Current code ports headless transition, beam charge, melee knockback, ranged orb baseline, spawn/headless transition timers, spawn-timer healing, iron-golem step/throw sounds, movement block-crack particles, low-hardness block breaking, and `BlockLoot` stomping. Acceptance still requires runtime evidence for headless phase timing, movement block breaking, and drops/sounds.

**Что нужно доделать:**
Port server-visible movement/block/spawn-timer behavior and validate headless beam combat in game.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Validate initial spawn/headless spawn timer behavior in the current `EntityThaumcraftBoss` model.
- Validate low-hardness block breaking and `BlockLoot` interaction now that `BlockLoot` is restored by GAP-7.
- Validate footstep/attack sounds with current sound events.
- Runtime scenario: spawn Eldritch Golem, damage below lethal threshold, trigger headless transition, verify explosion, beam charge, orb attack, melee knockback, drops/sounds, and no crash.

**Критерии приемки:**
- [ ] Headless transition occurs once and persists after reload.
- [ ] Headless beam/orb behavior matches reference timing and damage in combat.
- [ ] Movement block-breaking and sounds match reference or are explicitly justified for 1.12.2.

**Риски / зависимости:**
Depends on `BlockLoot` if the reference block-breaking behavior is restored. Client arc/vent particles are Phase 8.

### GAP-9: Projectiles miss impact sounds/status effects and need parity checks

**Статус:** частично реализовано  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/projectile/EntityGolemOrb.java:24-92`
- `src/main/java/thaumcraft/common/entities/projectile/EntityEldritchOrb.java:21-55`
- `src/main/java/thaumcraft/common/entities/projectile/EntityPechBlast.java:29-71`
- `src/main/java/thaumcraft/common/entities/projectile/*.java`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/projectile/EntityGolemOrb.class`
- `thaumcraft_src/thaumcraft/common/entities/projectile/EntityEldritchOrb.class`
- `thaumcraft_src/thaumcraft/common/entities/projectile/EntityPechBlast.class`
- `thaumcraft_src/thaumcraft/common/entities/projectile/*.class`

**Что не совпадает:**
Core damage/effect logic for `EntityGolemOrb`, `EntityEldritchOrb`, and `EntityPechBlast` is present. The 2026-05-15 projectile sound/status checkpoint restores server-audible Golem orb `shock`/`zap` sounds, Eldritch orb fizz sound/status byte `16`, next-tick Eldritch orb expiry, reference Golem orb squared-distance homing, and the original `0.1F` projectile collision border on Golem orb, Eldritch orb, and Pech blast. The 2026-05-15 projectile sweep restores Primal Orb drift/seeker/water-impact behavior, Shock Orb `shock`/`zap` sounds and block-Air placement search, Shock/Explosive/Primal collision borders, and Frost Shard constructor scatter. Pech blast impact remains visual-only in the reference, so its particle work is Phase 8.

**Что нужно доделать:**
Run runtime evidence for projectile sounds/status events, spawn data, damage, effects, and homing behavior.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Server-visible fixes are in `EntityGolemOrb`, `EntityEldritchOrb`, `EntityPechBlast`, `EntityPrimalOrb`, `EntityShockOrb`, `EntityExplosiveOrb`, and `EntityFrostShard`.
- Keep particle-only proxy calls deferred to Phase 8.
- Runtime scenario: fire Pech blast, eldritch orb, golem orb, primal/frost/shock/explosive projectiles and verify server damage/effects/sounds.

**Критерии приемки:**
- [x] Static server-visible projectile damage, potion effects, lifetime, gravity, homing/redirect, collision border, and impact sound/block side effects have been swept against reference for the documented projectile set.
- [x] Server-side Golem orb and Eldritch orb impact/redirect sounds/status are restored where the reference emits them.
- [ ] Projectile spawn data works across server/client without class cast or missing entity errors.

**Риски / зависимости:**
Client particles/renderers are Phase 8. Sounds depend on `TCSounds` entries and assets already present in `sounds.json`.

### GAP-10: Boss special attacks and phase flows are not runtime-validated

**Статус:** server-side baseline improved; runtime evidence open
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistLeader.java:113-166`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortal.java:104-293`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchWarden.java:108-149`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchWarden.java:157-298`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityTaintacleGiant.java`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityThaumcraftBoss.java`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/monster/boss/EntityCultistLeader.class`
- `thaumcraft_src/thaumcraft/common/entities/monster/boss/EntityCultistPortal.class`
- `thaumcraft_src/thaumcraft/common/entities/monster/boss/EntityEldritchWarden.class`
- `thaumcraft_src/thaumcraft/common/entities/monster/boss/EntityTaintacleGiant.class`
- `thaumcraft_src/thaumcraft/common/entities/monster/boss/EntityThaumcraftBoss.class`

**Что не совпадает:**
Cultist Leader, Eldritch Warden, and Cultist Portal have substantial server baselines, but acceptance requires runtime evidence. Warden field frenzy, teleport-home, absorption regeneration, warp application, cultist targeting, eldritch field block placement, and portal minion/boss sequence are all behavior-sensitive. The 2026-05-15 Cultist baseline checkpoint restores base Cultist size/XP/navigation/follow/move attributes and Cultist Knight reference max health, but full Cultist equipment parity remains blocked by missing separate armor piece items. The 2026-05-15 boss baseline checkpoint ports `EntityThaumcraftBoss` spawn/home/anger/enrage/regen/aggro/player-scaling/drop behavior and removes the server-behavior TODOs from that class.

**Что нужно доделать:**
Perform targeted runtime validation for every boss phase and fix behavior mismatches found. Continue auditing boss subclasses for remaining server-visible reference deltas.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Compare boss subclasses against reference for remaining spawn timer, phase, boss health/name/champion/enrage mechanics.
- Spawn each boss on a dedicated server and force combat states.
- Validate Cultist Leader ranged orb and cultist strength aura.
- Validate Cultist Portal stage progression, minion types, boss spawn, collision damage, drops, and death explosion.
- Validate Eldritch Warden orb/screech split, wither/weakness/warp, field frenzy, teleport-home, absorption regeneration, and immunity rules.
- Validate Taintacle Giant behavior or document why it is deferred/unused.

**Критерии приемки:**
- [ ] Each boss can be spawned and fought through its special behavior without server crash.
- [ ] Boss NBT and phase state persist through save/load where reference persists them.
- [ ] Every TODO inside boss server behavior is removed, fixed, or documented as Phase 8 visual-only.

**Риски / зависимости:**
Client renderer/particles are Phase 8. Outer Lands location/portal setup is a Stage 7 dependency for natural Warden scenarios, but direct spawn/combat validation is Stage 6.

### GAP-11: Pech, mob, and boss drops/sounds are not fully verified; some sounds are null

**Статус:** частично реализовано / требует проверки  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/monster/EntityPech.java:382-413`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistLeader.java:162-166`
- `src/main/java/thaumcraft/common/entities/monster/EntityEldritchGuardian.java:136-184`
- `src/main/java/thaumcraft/common/entities/monster/EntityCultist.java:37-39`
- `src/main/java/thaumcraft/common/entities/monster/EntityTaintSwarm.java:16`
- `src/main/java/thaumcraft/common/lib/TCSounds.java:18-96`
- `src/main/resources/assets/thaumcraft/sounds.json:1-68`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/monster/*.class`
- `thaumcraft_src/thaumcraft/common/entities/monster/boss/*.class`
- `thaumcraft_src/assets/thaumcraft/sounds/**`

**Что не совпадает:**
Pech death loot has a baseline and uses mana beans/coins/knowledge fragments (`EntityPech.java:390-413`), but runtime drops are unverified. Cultist Leader drops a loot bag (`EntityCultistLeader.java:162-166`). Eldritch Guardian drops essences/equipment (`EntityEldritchGuardian.java:162-184`). The 2026-05-15 checkpoint restores base Cultist common/rare drops and fixes Taint Swarm to the reference 50% taint-slime-only drop. Follow-up 2026-05-16 correction removes explicit `null` ambient/hurt/death overrides from `EntityCultist`: reference has no Cultist sound override, so the class should inherit hostile defaults from the base mob class. Static coverage guards now also check `TCSounds` registrations against `sounds.json` keys and bundled `.ogg` assets, and enforce an allowlist for monster/boss `null` sound overrides (currently only `EntityTaintSwarm#getAmbientSound`); runtime sound/drop evidence remains open.

**Что нужно доделать:**
Continue the broader Stage 6 drop/sound audit and run representative kill/combat scenarios.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Build the remaining table of each monster/boss `getAmbientSound`, `getHurtSound`, `getDeathSound`, `dropFewItems`, `dropEquipment`, and XP behavior.
- Keep reference-confirmed silent sounds documented instead of replacing them.
- Verify all referenced `TCSounds` names exist in `sounds.json` and asset files under `src/main/resources/assets/thaumcraft/sounds/` or copied from `thaumcraft_src/assets/`.
- Runtime scenario: kill representative Pech, cultists, eldritch mobs, taint mobs, golems/trunks, and bosses with/without looting.

**Критерии приемки:**
- [ ] Drop outputs match reference for representative mobs and bosses.
- [ ] All non-null sound events resolve and play without missing sound warnings.
- [x] Base Cultist no longer overrides ambient/hurt/death with explicit `null`; Taint Swarm ambient silence remains documented from reference.

**Риски / зависимости:**
Loot bag contents and broader content rewards may depend on Stage 9 content registration, but entity-side drop trigger and item ids are Stage 6.

### GAP-12: Entity registration identity and spawn eggs need compatibility review

**Статус:** требует проверки  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigEntities.java:32-49`
- `src/main/java/thaumcraft/common/config/ConfigEntities.java:55-129`
- absent file: `src/main/java/thaumcraft/common/entities/ItemSpawnerEgg.java`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/ItemSpawnerEgg.class`
- `thaumcraft_src/thaumcraft/common/entities/ItemSpawnerEgg$EntityEggStuff.class`
- `thaumcraft_src/thaumcraft/common/config/ConfigEntities.class` if decompiled from jar/source material as needed

**Что не совпадает:**
All core current entity classes are registered with Forge 1.12 `EntityEntryBuilder` using lowercase legacy-token registry paths like `thaumcraft:pech`, `thaumcraft:eldritchgolem`, and `thaumcraft:taintacletiny`. Reference 1.7.10 used sequential local mod ids plus `ItemSpawnerEgg.addMapping(...)`. The 2026-05-15 registry review documents the Stage 6 mapping below and treats Forge 1.12 entity eggs as the fresh-world replacement for the absent custom `ItemSpawnerEgg`; runtime registry smoke is still blocked by the environment timeout.

**Что нужно доделать:**
Run registry smoke and spawn-egg scenarios once the smoke environment reaches Forge readiness.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Reference/current Stage 6 mapping table is recorded in checkpoint 8.2.9 below.
- The custom 1.7.10 `ItemSpawnerEgg` is intentionally replaced by Forge 1.12 eggs for fresh-world scope.
- Run registry smoke to verify all entity entries register once and spawn eggs spawn correct entity classes.

**Критерии приемки:**
- [x] Every Stage 6 entity has documented current registry name and spawn path.
- [x] Spawn egg behavior is documented as a Forge-compatible replacement for fresh worlds.
- [ ] No duplicate/missing entity registration warnings occur during runtime smoke.

**Риски / зависимости:**
Entity id remapping for external saves is not a Stage 6 target. Keep this gap scoped to Stage 6 entity identity and spawnability on fresh worlds.

### GAP-13: Inhabited Zombie crab-spawn scenario needs runtime validation

**Статус:** требует проверки  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/monster/EntityInhabitedZombie.java:17-47`
- `src/main/java/thaumcraft/common/entities/monster/EntityEldritchCrab.java`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/monster/EntityInhabitedZombie.class`
- `thaumcraft_src/thaumcraft/common/entities/monster/EntityEldritchCrab.class`

**Что не совпадает:**
Current implementation spawns an `EntityEldritchCrab` with helm and suppresses normal zombie drops. The 2026-05-15 checkpoint restores reference death-update termination after the manual crab/XP path, reference attributes, Cultist targeting, local spawn-density guard, crabtalk/hurt sounds, and crab helm persistence. Runtime validation is still required for exact spawn count, XP, and save/reload behavior.

**Что нужно доделать:**
Run dedicated-server evidence for death update ordering and spawned crab behavior.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Spawn Inhabited Zombie, kill it by player and non-player damage.
- Verify exactly one helm crab spawns at the correct position.
- Verify XP rules and no duplicate zombie drops.
- Save/reload spawned crab and verify helm state persists.

**Критерии приемки:**
- [ ] Player kill spawns one helmed Eldritch Crab and expected XP.
- [ ] Non-player kill follows reference XP/drop semantics.
- [x] Server-side helm state is persisted through `EntityEldritchCrab` NBT.
- [ ] Crab helm state survives save/load in a runtime world.

**Риски / зависимости:**
Client crab renderer is Phase 8; server entity state and spawn count are Stage 6. Original Inhabited Zombie cultist helmet/legs/chest spawn equipment cannot be fully restored until separate cultist armor piece items exist in this branch.

### GAP-14: Runtime smoke/manual validation for Stage 6 has not been run

**Статус:** требует проверки  
**Критичность:** high

**Текущая реализация:**
- `docs/PRD.md:325-338`
- `AGENTS.md` runtime smoke validation rules

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/**`
- Original 1.7.10 runtime behavior from `Thaumcraft-1.7.10-4.2.3.5.jar`

**Что не совпадает:**
PRD explicitly says Stage 6 is not closed or validated and that runtime/manual spawn and combat checks remain required (`docs/PRD.md:325-326`). Current analysis was static plus targeted decompile only; no `compileJava`, `smoke-server`, or manual combat/spawn validation was run for this document.

**Что нужно доделать:**
After blocker/high implementation gaps are fixed, run focused build and runtime validation.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Run `./scripts/dev.sh compileJava`.
- Run `./scripts/dev.sh smoke-server` for common/server changes.
- Run a manual or scripted dedicated-server scenario matrix for Pech, golems, projectiles, bosses, Inhabited Zombie, drops, and sounds.
- Run client smoke only for Phase 8 renderer work or if Stage 6 changes add client references.

**Критерии приемки:**
- [ ] `compileJava` passes after Stage 6 fixes.
- [ ] `smoke-server` reaches normal ready state with no crash markers.
- [ ] Stage 6 manual scenario matrix is recorded with pass/fail evidence.

**Риски / зависимости:**
Heavy validation can be blocked by environment, but Stage 6 parity cannot be claimed without runtime evidence.

## 6. Итоговый checklist закрытия Stage 6

- [ ] Register/port golem and trunk items required to reach golem/trunk entity scenarios.
- [ ] Port golem placement, trunk placement, bell, core, upgrade, decoration, healing, GUI, and NBT behavior.
- [ ] Validate and fix each golem core AI flow: gather, empty, pickup, harvest, attack, fluid, essentia, lumber, use, butcher, sort, fish.
- [x] Port Pech spawn equipment/type setup, group anger, target reacquisition, and trade output generation.
- [ ] Verify Pech pickup/taming/combat/death loot on a dedicated server.
- [ ] Replace Cultist Portal vanilla chest placeholder with reference-compatible Thaumcraft loot block behavior.
- [ ] Validate Cultist Portal full stage sequence, minion spawning, boss spawning, drops, collision damage, and death explosion.
- [ ] Complete or justify Eldritch Golem missing spawn timer/block-breaking/sound behavior.
- [ ] Validate Eldritch Warden field frenzy, teleport-home, absorption, warp, orb/screech attack, and immunity rules.
- [ ] Validate Cultist Leader ranged attack, aura, equipment, team rules, and loot.
- [ ] Audit all projectile classes for damage/effects/lifetime/gravity/spawn data/impact sounds.
- [ ] Audit all Stage 6 entity drops, XP, and sounds against reference.
- [ ] Review entity registry names and spawn egg behavior for compatibility.
- [ ] Run `./scripts/dev.sh compileJava` after implementation fixes.
- [ ] Run `./scripts/dev.sh smoke-server` after runtime-affecting fixes.
- [ ] Record manual scenario evidence for Stage 6 before any parity claim.

## 7. Definition of Done

Stage 6 считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 6 реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 6;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 6 проверены вручную или тестами;
- ./docs/Stage6.md обновлен и не содержит критичных открытых вопросов.

## 8. Решения по ранее открытым вопросам

### 8.1 Entity id / spawn egg compatibility policy

Утвержденная policy для Stage 6:

- Forge 1.12.2 registry names из `ConfigEntities` считаются canonical для текущего порта: `thaumcraft:<lower_snake_name>`.
- Reference 1.7.10 names вроде `Thaumcraft.BrainyZombie` считаются source names и должны быть отражены в identity mapping, но не заменяют current registry names.
- Текущий порядок и набор Stage 6 entity registrations в `ConfigEntities` нужно считать стабильным: новые entries добавлять append-only, без reorder, если нет явно задокументированной compatibility reason.
- Старый custom `ItemSpawnerEgg` не портируется как blocker Stage 6. Forge 1.12 spawn eggs являются основной реализацией spawn egg behavior.
- Legacy `ItemSpawnerEgg` можно вернуть позже только как отдельный compatibility checkpoint, если появится конкретное addon/runtime requirement.

Практическое следствие для GAP-12: закрывать через mapping table `reference entity name/id/egg colors -> current registry name/egg colors`, registry smoke, и явное документирование замены старого egg item на Forge eggs.

### 8.2 `BlockLoot` ownership and Stage 6 closure policy

Утвержденная policy для Stage 6:

- `BlockLoot` может принадлежать content/block checkpoint по ownership, но для Stage 6 это прямой blocker, потому что `EntityCultistPortal` сейчас меняет server-visible boss reward behavior vanilla chest placeholder'ом.
- Stage 6 нельзя считать закрытой, пока Cultist Portal stage 0 ставит vanilla chests вместо reference-compatible Thaumcraft loot crates.
- Минимально приемлемое закрытие: port `BlockLoot`, `BlockLootItem`, `blockLootUrn`, `blockLootCrate`, регистрацию block/item, loot generation path, и замену placeholder в `EntityCultistPortal`.
- Safe shell без реального loot behavior не считается parity closure. Если полноценные loot pools зависят от Stage 9/content registration, это нужно явно отметить как dependency, но не скрывать пустым блоком.

Практическое следствие для GAP-7: делать отдельный scoped checkpoint `BlockLoot + Cultist Portal replacement`; после него запускать `compileJava`, `smoke-server`, и manual Cultist Portal scenario.

### 8.2.1 BlockLoot checkpoint status — 2026-05-14

Статус: direct placeholder replacement closed; full Stage 6 parity remains open.

Что сделано:

- Добавлены `BlockLoot` и `BlockLootItem` для rarity metas `0..2`.
- Зарегистрированы `ConfigBlocks.blockLootUrn` и `ConfigBlocks.blockLootCrate` с item blocks.
- `EntityCultistPortal` stage 0 больше не ставит vanilla chest placeholder; теперь ставит `ConfigBlocks.blockLootCrate` с reference rarity meta.
- `EntityCultistPortal` stage 0 теперь также записывает reference `TileBanner.facing` values для четырех generated banners после появления `TileBanner.setFacing(...)`.
- `Utils.generateLoot(...)` добавляет общий reward path для urn/crate drops и использует текущие loot bag tables, enchanted book/gear branches, и coin fallback.
- Оригинальные urn/crate textures скопированы из `thaumcraft_src/assets/thaumcraft/textures/blocks/`; Forge 1.12.2 blockstate/model fallbacks добавлены до Stage 8 renderer parity.

Проверки:

- `./scripts/dev.sh compileJava` — passed после исправления 1.12.2 signature mismatch в `BlockLoot.getSubBlocks(...)`.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` и `THAUMCRAFT_SMOKE_TIMEOUT=300s ./scripts/dev.sh smoke-server` — оба завершились timeout before ready state; `run/smoke-server.log` остановился сразу после `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, новых crash reports и mod-load crash markers нет.
- Clean recon commit `da3f307` был проверен в отдельном worktree и воспроизвел тот же smoke timeout до mod loading, без crash reports/markers; текущий smoke result классифицирован как pre-existing smoke wrapper/runtime environment failure, а не как BlockLoot regression.

Оставшиеся ограничения:

- `Config.initLoot()` / full loot pool parity остается Stage 9/content dependency; текущий path не оставляет пустой safe shell, но не доказывает полный reference reward distribution.
- Manual `S6-BOSS-01` Cultist Portal scenario не выполнен по контрактному исключению GUI/user-driven/manual validation.
- Stage 6 в целом остается open до закрытия остальных blocker/high gaps и runtime/manual evidence.

### 8.2.2 Cultist Portal banner facing checkpoint — 2026-05-14

Статус: direct banner-facing TODO closed; full Stage 6 parity remains open.

Что сделано:

- После появления persistent `TileBanner.facing` в Stage 7 ring checkpoint `EntityCultistPortal` больше не оставляет TODO при генерации stage-0 banners.
- Портирована reference mapping `2 -> 8`, `3 -> 0`, `4 -> 12`, `5 -> 4` для четырех `EnumFacing.byIndex(a)` banner positions.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; новых crash reports и mod-load crash markers нет.

Оставшиеся ограничения:

- `S6-BOSS-01` runtime/manual Cultist Portal stage progression remains unverified.
- Full boss/minion combat, drops, and loot distribution parity remains open.

### 8.2.3 Pech trade output checkpoint — 2026-05-15

Статус: server-side offer generation implemented; client GUI/runtime evidence remains open.

Что сделано:

- `ContainerPech.enchantItem(..., 0)` now runs the reference trade roll path for the existing one-input/four-output Pech inventory.
- Added current-port Pech trade tables by Pech type with available equivalents for reference outputs.
- Restored input value splitting, pack-loot one-item offers, high-value shared loot offers, input consumption after roll, de-tame chance with Pech trade sound, and `PechDrop` owner tagging for unclaimed GUI drops.

Проверки:

- `./scripts/dev.sh compileJava` — initially failed because `Items.ENCHANTED_BOOK` is typed as `Item` in 1.12.2; fixed by using `ItemEnchantedBook.getEnchantedItemStack(...)`, then passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- `GuiPech` remains Phase 8/client work; current `ClientProxy` still returns `null` for `GUI_PECH`.
- Original potion metadata/candle trade outputs are not directly available in the current branch and remain content/client dependencies rather than silently fabricated items.
- Manual `S6-PECH-02` trade scenario remains unrun.

### 8.2.4 Pech spawn variant checkpoint — 2026-05-15

Статус: spawn equipment/type baseline implemented; runtime evidence remains open.

Что сделано:

- Added `EntityPech.onInitialSpawn(...)` to run the reference random held-item setup.
- Restored wand Pech setup with Pech focus, starting primal vis, type `1`, and reduced mainhand drop chance.
- Restored bow Pech type `2`, melee/tool/fishing-rod equipment possibilities, difficulty-based enchantment for non-wand gear, pickup-loot chance, and combat-task refresh after spawn setup.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.

Оставшиеся ограничения:

- Type-specific Pech display-name localization is restored in checkpoint 8.2.6 below.
- Spawn variants have not been observed in a runtime world because smoke-server remains environment-blocked and manual scenarios are excluded.
- Pech group anger/trade runtime scenarios remain separate Stage 6 work.

### 8.2.5 Pech group anger checkpoint — 2026-05-15

Статус: server-side anger baseline implemented; runtime evidence remains open.

Что сделано:

- Restored the reference player-damage fan-out in `EntityPech.attackEntityFrom(...)`: damaging one Pech now angers nearby Pechs within the original `32 x 16 x 32` search volume.
- Added `EntityPech.becomeAngryAt(...)` to set revenge and attack targets, reset taming, refresh combat AI, set the original `400 + rand(400)` anger duration, and emit the Pech charge status/sound when entering anger.
- Updated `EntityPech.onUpdate()` so angry Pechs decrement anger, reacquire the revenge target while angry, and drop the player attack target when the anger timer expires.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.

Оставшиеся ограничения:

- Group anger, charge sound/status, and anger expiry have not been observed in a runtime world because smoke-server remains environment-blocked and manual scenarios are excluded.
- Client angry particle/status handling remains Phase 8/client work if reference visual parity is required beyond the server status byte.

### 8.2.6 Pech type-name checkpoint — 2026-05-15

Статус: type-specific display-name baseline implemented; runtime evidence remains open.

Что сделано:

- Restored the reference `PechType` name switch in `EntityPech.getName()`, preserving custom names before falling back to type localization.
- Added the original English Pech type strings to `en_us.lang`: `Pech Forager`, `Pech Mage`, and `Pech Stalker`.
- Added lowercase 1.12 entity-name aliases beside the original `entity.Thaumcraft.Pech.*` keys so normal entity translation paths have matching strings.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.

Оставшиеся ограничения:

- Type names have not been observed over an in-game entity because smoke-server remains environment-blocked and manual scenarios are excluded.
- Natural/command-spawn variant runtime coverage remains open under S6-PECH-01.

### 8.2.7 Projectile sound/status checkpoint — 2026-05-15

Статус: server-visible projectile sound/status baseline improved; runtime evidence remains open.

Что сделано:

- Restored `EntityGolemOrb` reference impact sound by replacing the generic block `playEvent` placeholder with `TCSounds.SHOCK`.
- Restored `EntityGolemOrb` redirect sound with `TCSounds.ZAP` and changed homing acceleration back to the reference squared-distance divisor.
- Restored `EntityEldritchOrb` reference impact fizz sound, status byte `16`, and next-tick expiry instead of immediate `setDead()`.
- Restored the reference `0.1F` collision border on `EntityGolemOrb`, `EntityEldritchOrb`, and `EntityPechBlast`.
- Classified `EntityPechBlast` impact particles as Phase 8 visual work because the reference has no server-side impact sound/status for that projectile.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.

Оставшиеся ограничения:

- Projectile sound/status behavior has not been observed in a runtime world because smoke-server remains environment-blocked and manual scenarios are excluded.
- Client burst/wisp particle rendering for Golem orb, Eldritch orb, and Pech blast remains Phase 8/client work.
- Broader projectile sweep for primal/frost/shock/explosive projectiles remains open under S6-PROJ-01.

### 8.2.8 Cultist and swarm drops checkpoint — 2026-05-15

Статус: focused drop/silent-sound baseline improved; runtime evidence remains open.

Что сделано:

- Restored base `EntityCultist.dropFewItems(...)` common drops from the reference: knowledge fragment, void seed, and coin rolls shared by Cultist Knight, Cultist Cleric, and Cultist Leader's `super.dropFewItems(...)` path.
- Added the 1.12-compatible equivalent of the reference base Cultist rare eldritch-object drop using the same recently-hit/looting rare-drop chance pattern already used elsewhere in the port.
- Fixed `EntityTaintSwarm.dropFewItems(...)` to match the reference 50% taint-slime drop and removed the non-reference guaranteed taint-tendril fallback.
- Documented reference sound baselines: `EntityCultist` has no reference sound override (should inherit hostile defaults), and Taint Swarm ambient returns an empty sound string in 1.7.10.

Проверки:

- `./scripts/dev.sh compileJava` — initially failed because `dropRareDrop(int)` is not a 1.12 superclass hook; fixed by moving the rare drop into `dropFewItems(...)`, then passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.

Оставшиеся ограничения:

- Cultist and Taint Swarm drops/sounds have not been observed in a runtime world because smoke-server remains environment-blocked and manual scenarios are excluded.
- The broader Stage 6 drop/sound table remains open for other mobs and bosses.

### 8.2.9 Stage 6 entity registry mapping checkpoint — 2026-05-15

Статус: static registry/egg mapping documented; runtime smoke remains open.

Решение:

- The active target is fresh worlds, so the absent 1.7.10 `ItemSpawnerEgg` item is not being ported solely for legacy item compatibility.
- Forge 1.12 entity eggs from `EntityEntryBuilder.egg(...)` are the documented replacement for Stage 6 spawn eggs.
- Current registry paths are lowercase legacy tokens from `ConfigBlocks.legacyPath(...)`; they are not snake_case.

Mapping:

| Reference token | Current registry name | Current local order | Egg mapping |
|---|---|---:|---|
| `Golem` | `thaumcraft:golem` | 17 | none in reference/current |
| `TravelingTrunk` | `thaumcraft:travelingtrunk` | 18 | none in reference/current |
| `BrainyZombie` | `thaumcraft:brainyzombie` | 19 | `0xFFC0FF / 0x008000` |
| `GiantBrainyZombie` | `thaumcraft:giantbrainyzombie` | 20 | `0xFFC0FF / 0x004000` |
| `Wisp` | `thaumcraft:wisp` | 21 | `0xFFC0FF / 0xFFFFFF` |
| `Firebat` | `thaumcraft:firebat` | 22 | `0xFFC0FF / 0xC00000` |
| `Pech` | `thaumcraft:pech` | 23 | `0xFFC0FF / 0x400040` |
| `MindSpider` | `thaumcraft:mindspider` | 24 | `0xAAAAAA / 0x404040` |
| `EldritchGuardian` | `thaumcraft:eldritchguardian` | 25 | `0x222222 / 0x404040` |
| `EldritchWarden` | `thaumcraft:eldritchwarden` | 26 | `0x552222 / 0x404040` |
| `CultistKnight` | `thaumcraft:cultistknight` | 27 | `0xFF5055 / 0x000080` |
| `CultistCleric` | `thaumcraft:cultistcleric` | 28 | `0xFF5055 / 0x800000` |
| `CultistLeader` | `thaumcraft:cultistleader` | 29 | `0xFF5055 / 0x505050` |
| `CultistPortal` | `thaumcraft:cultistportal` | 30 | `0xFF5055 / 0xFF50FF` |
| `EldritchGolem` | `thaumcraft:eldritchgolem` | 31 | `0x555555 / 0x404040` |
| `EldritchCrab` | `thaumcraft:eldritchcrab` | 32 | `0x555555 / 0x550000` |
| `InhabitedZombie` | `thaumcraft:inhabitedzombie` | 33 | `0x557755 / 0x550000` |
| `ThaumSlime` | `thaumcraft:thaumslime` | 34 | `0xFFC0FF / 0xFF80FF` |
| `TaintSpider` | `thaumcraft:taintspider` | 35 | `0xFFC0FF / 0x404040` |
| `Taintacle` | `thaumcraft:taintacle` | 36 | `0xFFC0FF / 0x800080` |
| `TaintacleTiny` | `thaumcraft:taintacletiny` | 37 | `0xFFC0FF / 0x800090` |
| `TaintSpore` | `thaumcraft:taintspore` | 38 | `0xFFC0FF / 0x800070` |
| `TaintSwarmer` | `thaumcraft:taintswarmer` | 39 | `0xFFC0FF / 0x800060` |
| `TaintSwarm` | `thaumcraft:taintswarm` | 40 | `0xFFC0FF / 0x800050` |
| `TaintedChicken` | `thaumcraft:taintedchicken` | 41 | `0xFFC0FF / 0xC0C0C0` |
| `TaintedCow` | `thaumcraft:taintedcow` | 42 | `0xFFC0FF / 0x7E3C3B` |
| `TaintedCreeper` | `thaumcraft:taintedcreeper` | 43 | `0xFFC0FF / 0x00FF00` |
| `TaintedPig` | `thaumcraft:taintedpig` | 44 | `0xFFC0FF / 0xEF99EF` |
| `TaintedSheep` | `thaumcraft:taintedsheep` | 45 | `0xFFC0FF / 0x808080` |
| `TaintedVillager` | `thaumcraft:taintedvillager` | 46 | `0xFFC0FF / 0x00FFFF` |
| `TaintacleGiant` | `thaumcraft:taintaclegiant` | 47 | `0xFFC0FF / 0x808080` |

Проверки:

- `git diff --check` — passed.
- No compile/build/smoke command required for this documentation-only checkpoint.

Оставшиеся ограничения:

- Runtime registry smoke remains blocked by the pre-Forge smoke-server timeout, so duplicate/missing registry warnings and actual Forge egg spawning remain unobserved.
- External 1.7.10 save/item compatibility remains out of scope for the active fresh-world target.

### 8.2.10 Inhabited Zombie crab-spawn checkpoint — 2026-05-15

Статус: server-side crab-spawn baseline improved; runtime evidence remains open.

Что сделано:

- Restored `EntityInhabitedZombie` reference attributes: 30 health, 5 attack damage, and zero zombie reinforcement chance.
- Restored the reference target hooks for retaliation and Cultist targeting.
- Changed `EntityInhabitedZombie.onDeathUpdate()` to finish with `setDead()` after the manual crab/XP/particle path instead of calling vanilla zombie death update, preventing delayed duplicate vanilla death handling after the crab spawn.
- Restored the reference local spawn-density guard for Inhabited Zombies.
- Restored reference crabtalk ambient and generic hostile hurt sounds for Inhabited Zombie.
- Restored `EntityEldritchCrab` helm NBT persistence, hard/random natural helm initialization, 0.8 x 0.6 size, 6 XP value, 4 attack damage, helm-dependent armor/speed, and cultist-plate drop when the helm breaks below half health.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.

Оставшиеся ограничения:

- Inhabited Zombie kill scenarios and crab save/reload have not been observed in a runtime world because smoke-server remains environment-blocked and manual scenarios are excluded.
- Original Inhabited Zombie cultist helmet/legs/chest spawn equipment remains a content dependency because this branch currently exposes aggregate cultist armor items rather than the original separate helmet/legs/chest fields.

### 8.2.11 Cultist baseline attributes checkpoint — 2026-05-15

Статус: focused Cultist server baseline improved; runtime evidence remains open.

Что сделано:

- Restored base `EntityCultist` size `0.6 x 1.8`, 10 XP value, break/enter-door navigation flags, 32-block follow range, and 0.3 movement speed from the reference.
- Removed the non-reference base Cultist max-health override so subclass max-health values control health as in the reference.
- Restored `EntityCultistKnight` max health from `35` to the reference `36`.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.

Оставшиеся ограничения:

- Cultist runtime combat/team/equipment scenarios remain unobserved because smoke-server remains environment-blocked and manual scenarios are excluded.
- Cultist Knight attack/armor placeholders are intentionally left unchanged in this checkpoint until the missing separate reference armor piece items are resolved.

### 8.2.12 Base boss behavior checkpoint — 2026-05-15

Статус: `EntityThaumcraftBoss` server TODOs replaced with reference-derived behavior; runtime evidence remains open.

Что сделано:

- Restored base boss XP, home NBT persistence, spawn-home assignment, spawn-timer invulnerability/push suppression, air-supply immunity, non-despawn behavior, and eldritch-mob team rule.
- Restored boss anger tracking, over-35-damage cap, strength/resistance/speed enrage buffs, localized enrage player message, anger particles, passive regeneration, aggro accounting, target reassessment, and player-count health/damage scaling.
- Restored inherited boss reward drops for Eldritch Golem/Warden-style bosses: eldritch object meta `3` plus rare loot bag.
- Kept Cultist Leader's override reference-compatible by removing the inherited base-boss drop call, leaving the leader rare loot bag only.
- Restored Eldritch Golem spawn/headless transition timers and Warden spawn timer/status trigger so the new base spawn invulnerability path is exercised by the subclasses that used it in 1.7.10.
- Added the missing English `tc.boss.enrage` localization key.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.

Оставшиеся ограничения:

- Boss combat, aggro retargeting, player scaling, spawn invulnerability, and reward drops have not been observed in a runtime world because smoke-server remains environment-blocked and manual scenarios are excluded.
- Champion-name parity remains a separate dependency, but the custom `EntityUtils.CHAMPION_MOD` attribute path is no longer missing: a later checkpoint restored the original early `EntityConstructing` registration so saved champion mobs no longer lose `tc.mobmod` during NBT attribute load.
- Eldritch Golem low-hardness block-breaking / `BlockLoot` stomping is restored by checkpoint 8.2.13 below, but remains runtime-unobserved.

### 8.2.13 Eldritch Golem movement checkpoint — 2026-05-15

Статус: focused Eldritch Golem server-visible movement behavior restored; runtime evidence remains open.

Что сделано:

- Restored the reference iron-golem step sound override for Eldritch Golem movement.
- Restored movement block-crack particles using the current block state id.
- Restored server-side `BlockLoot` destruction when the Golem walks over loot blocks.
- Restored server-side low-hardness block breaking for blocks directly in the moving Golem's path.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.

Оставшиеся ограничения:

- Eldritch Golem movement block breaking, `BlockLoot` stomping, and step sound have not been observed in a runtime world because smoke-server remains environment-blocked and manual scenarios are excluded.
- Headless combat timing and save/reload persistence still need runtime scenario evidence before GAP-8 can close.

### 8.2.14 Projectile sweep checkpoint — 2026-05-15

Статус: additional projectile server-visible reference behavior restored; runtime evidence remains open.

Что сделано:

- Restored `EntityPrimalOrb` non-seeker random drift, seeker targeting against nearest non-owner living entity, squared-distance seeker acceleration, water material impact trigger, and `0.1F` collision border.
- Restored `EntityShockOrb` `shock` impact sound, `zap` redirect sound, `0.1F` collision border, thrower-inclusive area damage search, and reference-like block-Air placement scan with line-of-sight check.
- Restored `EntityExplosiveOrb` `0.1F` collision border.
- Restored `EntityFrostShard` constructor scatter application so scattershot/normal/boulder focus paths pass their requested inaccuracy into the projectile.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.

Оставшиеся ограничения:

- Projectile damage/effect/sound/block side effects have not been observed in a runtime world because smoke-server remains environment-blocked and manual scenarios are excluded.
- Client particles for projectile trails/impacts remain Phase 8 FX work.

### 8.2.15 Golem item registration checkpoint — 2026-05-15

Статус: golem/trunk item registration and metadata surface restored; placement/workflows remain open.

Что сделано:

- Added append-only `ConfigItems` registrations for `TrunkSpawner`, `ItemGolemPlacer`, `ItemGolemCore`, `ItemGolemUpgrade`, `GolemBell`, and `ItemGolemDecoration` using the original TC4 registry/translation tokens.
- Restored creative metadata ranges for golem placer metas `0..7`, golem core blank meta `100` plus metas `0..11`, golem upgrades `0..5`, decorations `0..7`, and trunk meta `0`.
- Restored reference stack-size/subtype basics for golem placers, trunks, bell, cores, upgrades, and decorations.
- Restored golem core GUI/inventory helper metadata parity and added the reference decoration metadata-to-character helper for later decoration workflow work.
- Added English localization for golem/trunk item names and golem upgrade descriptions.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- `ItemGolemPlacer` still needs the reference on-use spawn path: owner/home/facing/core/upgrades/advanced/deco/markers/inventory transfer, setup, spawn, sound/status, and stack consume behavior.
- `ItemTrunkSpawner` still needs the reference on-use spawn path: owner/custom name/upgrade/inventory transfer, spawn, living initialization, and stack consume behavior.
- `ItemGolemBell` and `EntityGolemBase` still need a dedicated bell marker/linking, decoration, healing, and wheat interaction parity pass.
- Golem/trunk placement has not been observed in a runtime world because smoke-server remains environment-blocked and manual scenarios are excluded.

### 8.2.16 Golem/trunk placement checkpoint — 2026-05-15

Статус: golem/trunk item spawn paths ported; interaction parity and runtime evidence remain open.

Что сделано:

- Ported `ItemGolemPlacer` server-side placement: side-offset spawn position, golem type metadata, advanced/core/upgrades/deco/marker/inventory NBT restore, home/facing setup, owner assignment, custom name persistence, entity spawn, living sound, and survival stack consume behavior.
- Ported `ItemTrunkSpawner` server-side placement: side-offset spawn position, owner UUID assignment, custom name, upgrade/inventory NBT restore, living initialization, entity spawn, and survival stack consume behavior.
- Restored reference golem type metadata ordering and baseline values so placer metas `0..7` map to Straw, Wood, Tallow, Clay, Flesh, Stone, Iron, and Thaumium.
- Added golem setup/read helpers for home-facing setup, inventory sizing, upgrade data sync, decoration data sync, and inventory persistence after reload.
- Added minimal traveling trunk upgrade/inventory-size and owner UUID persistence needed by the item spawn path.
- Added `ItemGolemBell.getMarkers(...)` item-NBT parsing for placer marker restore.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Bell marker editing/linking, decoration application/removal, golem healing/wheat interaction, and full trunk upgrade behavior remain open Stage 6 work.
- Runtime placement was not observed in a world because smoke-server remains environment-blocked and manual scenarios are excluded.
- Client models/rendering for the placed golems/trunks remain Phase 8 work.

### 8.2.17 Golem bell and decoration interaction checkpoint — 2026-05-15

Статус: core bell marker/linking and basic golem right-click interactions restored; bell pickup remains open.

Что сделано:

- Restored marker equality/fuzzy matching to include side and color like the reference, allowing side-specific and colored marker behavior.
- Ported `ItemGolemBell` golem linking via entity interaction, including marker copy, golem id, home coordinates, and home-facing NBT.
- Ported bell block marker editing with order-upgrade color cycling, shift-remove behavior, stale-link cleanup, golem marker synchronization, and orb feedback sound.
- Restored empty-marker reset behavior to write an empty marker list and clear the linked golem markers.
- Restored golem decoration application with reference conflict groups, camera-clack sound, server stack consumption, decoration data sync, and golem setup refresh.
- Restored wheat healing interaction and prevented bell/wand-held interactions from opening the golem GUI.
- Restored upgrade inventory resize after applying upgrades and bootup status after applying a core.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- `ItemGolemBell.onLeftClickEntity(...)` pickup/packing behavior for golems and traveling trunks is still open.
- Full traveling trunk upgrade behavior remains open beyond the minimal placement/persistence route.
- Runtime marker/deco/healing scenarios have not been observed because smoke-server remains environment-blocked and manual scenarios are excluded.
- Client marker visuals and golem/trunk rendering remain Phase 8 work.

### 8.2.18 Golem bell pickup checkpoint — 2026-05-15

Статус: bell left-click pickup/packing behavior restored; runtime evidence remains open.

Что сделано:

- Ported `ItemGolemBell.onLeftClickEntity(...)` pickup for traveling trunks, including owner check for upgrade `3`, upgrade item chance on sneak pickup, order-upgrade inventory packing, normal inventory drops when not packed, zap sound, and entity removal.
- Ported bell pickup for golems, including placer stack metadata, advanced/core/upgrades/deco/marker/inventory NBT packing, sneak pickup core/upgrades split-drop behavior, custom name restore path, carried-item drop, zap sound, and entity removal.
- Added `EntityGolemBase.dropStuff()` as the shared carried-item drop path used by normal drops and bell pickup.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Full traveling trunk upgrade behavior remains open beyond pickup/packing and placement persistence.
- Runtime pickup/packing scenarios have not been observed because smoke-server remains environment-blocked and manual scenarios are excluded.
- Client pickup animation/visual parity remains Phase 8 work.

### 8.2.19 Traveling trunk baseline checkpoint — 2026-05-15

Статус: additional server-visible traveling trunk baseline behavior restored; complex upgrade scenarios remain open.

Что сделано:

- Restored reference-like trunk baseline durability, attack damage attribute, fire immunity, persistence, and `0.8x0.8` size.
- Restored stay-aware owner following and faster follow speed for Air upgrade `0`.
- Restored player interaction branches for upgrade application, food healing, upgrade `3` owner access blocking, and GUI opening.
- Restored fall-damage immunity and upgrade `3` damage immunity.
- Restored passive healing and upgrade `3` accelerated healing.
- Restored upgrade `5` item pull behavior: nearby item attraction, insertion into trunk inventory, item entity remainder handling, eat sound, and status trigger.
- Restored inventory drops on normal trunk death.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Upgrade `2` owner-target defense/combat behavior and cross-dimension owner-follow transfer remain open.
- Runtime trunk upgrade, feeding, pickup, and inventory-drop scenarios have not been observed because smoke-server remains environment-blocked and manual scenarios are excluded.
- Client lid/heart/smoke animation parity remains Phase 8 work.

### 8.2.20 Golem fluid NBT and ranged sound checkpoint — 2026-05-15

Статус: small server-visible golem NBT/sound parity slice restored; runtime save/load evidence remains open.

Что сделано:

- Restored reference core `5` fluid-carried NBT persistence by writing and loading the `FluidStack` fields on the golem root tag.
- Restored reference golem toggle byte persistence through the `toggles` NBT key.
- Restored carried item data-manager sync after entity reload.
- Replaced the ranged attack sound TODO with `TCSounds.GOLEMIRONSHOOT` using the reference pitch formula.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime save/load evidence for fluid, toggle, and carried item display sync remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- The inactive pedestal/cosmetic-block state, death logging/bootup client sound parity, and carried fluid/essentia display sync remain open.
- Full per-core golem AI runtime scenarios remain open.

### 8.2.21 Golem inactive stone checkpoint — 2026-05-15

Статус: reference inactive golem-stone behavior restored; runtime placement evidence remains open.

Что сделано:

- Restored the original `onLivingUpdate` inactive-state check for golems standing on `ConfigBlocks.blockCosmeticSolid` meta `10` (`golemStoneActive`).
- Kept the restored behavior server-visible by reusing the current Forge 1.12 block-state metadata contract rather than introducing a new block or registry name.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.

Оставшиеся ограничения:

- Runtime confirmation that golems pause on active golem stone and resume off it remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Death logging/bootup client sound parity and carried fluid/essentia display sync remain open.
- Full per-core golem AI runtime scenarios remain open.

### 8.2.22 Traveling trunk defense checkpoint — 2026-05-15

Статус: upgrade `2` owner-target defense baseline restored; cross-dimension owner-follow remains open.

Что сделано:

- Restored the reference defensive anger timer for traveling trunks.
- Restored upgrade `2` target acquisition from the owner's revenge or attack target when the trunk is not staying.
- Added defensive target pursuit, melee damage using the trunk attack-damage attribute, hit status, and blaze-hit sound feedback.
- Cleared stale attack targets when anger expires or the target dies so normal owner following can resume.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation of owner-defense target acquisition and attack cadence remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Cross-dimension owner-follow transfer remains open.
- Client lid/heart/smoke animation parity remains Phase 8 work.

### 8.2.23 Traveling trunk dimension transfer checkpoint — 2026-05-15

Статус: linked cross-dimension owner-follow transfer restored; runtime transfer evidence remains open.

Что сделано:

- Added a server-side linked-trunk registry keyed by owner UUID in `EventHandlerEntity`, matching the original weak-reference linked entity pattern.
- Registered owned traveling trunks while they tick near their owner.
- Moved linked trunks to the owner player's destination world when the player joins a different world, preserving owner UUID, upgrade, stay flag, inventory, health, and custom name.
- Removed transferred source trunks only after the replacement entity successfully spawns in the target world.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation of cross-dimension trunk transfer remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Event-driven transfer depends on the trunk being linked to its owner while both are loaded before the dimension change, matching the original linked-entity model.
- Client lid/heart/smoke animation parity remains Phase 8 work.

### 8.2.24 Golem carried display sync checkpoint — 2026-05-15

Статус: carried item/fluid/essentia data-manager display sync restored; renderer parity remains Phase 8.

Что сделано:

- Added `EntityGolemBase.getCarriedForDisplay()` for the synced carried stack, matching the original data-watcher access pattern.
- Restored `updateCarried()` display sync for carried items.
- Restored fluid-core display sync by publishing the carried fluid block stack with the carried amount as metadata when a fluid block exists.
- Restored essentia-core display sync with a jar display stack populated through the current `IEssentiaContainerItem` jar item path.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation of carried item/fluid/essentia display changes remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Actual visual/render parity for displayed carried stacks remains Phase 8 work.
- Death logging and bootup client sound parity remain open.

### 8.2.25 Golem death logging checkpoint — 2026-05-15

Статус: server-side reference death log restored; runtime death scenario evidence remains open.

Что сделано:

- Restored the original server-side `EntityGolemBase.onDeath(...)` log line before vanilla death handling.
- Logged the golem instance, true damage source, and damage type through the current Thaumcraft logger using the Forge 1.12 `DamageSource` accessors.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation of the emitted death log remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Bootup client sound parity remains open and belongs with Phase 8 visual/client-side verification.
- Full per-core golem AI runtime scenarios remain open.

### 8.2.26 Golem fire resistance checkpoint — 2026-05-15

Статус: reference fire-resistance guards restored; runtime fire scenario evidence remains open.

Что сделано:

- Restored the explicit reference `setFire(...)` guard so fire-resistant golem types cannot be ignited.
- Restored the reference fire-damage rejection for fire-resistant golem types through the current `DamageSource.isFireDamage()` API.
- Made setup assign the vanilla fire-immunity flag from the current golem type instead of only ever setting it to true.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation of clay/stone/iron/thaumium fire immunity remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Bootup client sound parity remains open and belongs with Phase 8 visual/client-side verification.
- Full per-core golem AI runtime scenarios remain open.

### 8.2.27 Golem armor calculation checkpoint — 2026-05-15

Статус: reference golem type/decor armor calculation restored; runtime combat evidence remains open.

Что сделано:

- Restored `EntityGolemBase.getTotalArmorValue()` to include the golem type armor value from `EnumGolemType`.
- Restored the reference visor and plate decoration armor bonuses.
- Preserved the vanilla/reference armor cap at `20`.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation of type/decor armor damage reduction remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Bootup client sound parity remains open and belongs with Phase 8 visual/client-side verification.
- Full per-core golem AI runtime scenarios remain open.

### 8.2.28 Golem water pathing checkpoint — 2026-05-15

Статус: reference material-dependent water pathing adapted to Forge 1.12 APIs; runtime traversal evidence remains open.

Что сделано:

- Restored the original setup distinction where stone, iron, and thaumium golems do not avoid water while other golem types do.
- Adapted the original `PathNavigateGround` water-avoidance call to the available Forge 1.12 surface by pairing `setCanSwim(...)` with `PathNodeType.WATER` path priority.
- Kept the behavior local to `setupGolem()` so type changes and NBT reload setup refresh the pathing preference.

Проверки:

- `./scripts/dev.sh compileJava` — initially failed because `PathNavigateGround.setAvoidsWater(boolean)` is absent in this Forge 1.12 mapping; after adapting to `setCanSwim(...)` and `PathNodeType.WATER`, rerun passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation of water traversal and avoidance per golem material remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- This is a Forge 1.12 API adaptation of the reference water-avoidance knob, not direct use of the removed old method.
- Full per-core golem AI runtime scenarios remain open.

### 8.2.29 Golem no-drowning checkpoint — 2026-05-15

Статус: reference air-supply preservation restored; runtime drowning evidence remains open.

Что сделано:

- Restored `EntityGolemBase.decreaseAirSupply(...)` to return the current air value unchanged, matching the original golem no-drowning behavior.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation that submerged golems do not drown remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 8.2.30 Golem melee enchantment checkpoint — 2026-05-15

Статус: reference melee enchantment/knockback callbacks restored; runtime combat evidence remains open.

Что сделано:

- Restored held-item creature damage contribution for golem melee attacks using the Forge 1.12 `EnchantmentHelper.getModifierForCreature(...)` API.
- Restored enchantment knockback motion and golem counter-motion damping after successful melee hits.
- Restored thorns and arthropod enchantment callbacks around successful golem melee hits while preserving the existing fire-upgrade behavior.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation of golem melee damage, knockback, fire, thorns, and arthropod effects remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 8.2.31 Golem upgrade retaliation checkpoint — 2026-05-15

Статус: reference upgrade `5` retaliation restored; runtime combat evidence remains open.

Что сделано:

- Restored the reference upgrade `5` thorns-style retaliation when a golem is damaged by another entity.
- Retaliation damage now scales as `upgradeCount * 2 + random.nextInt(2 * upgradeCount)` and plays the thorns hit sound on the attacker.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation of upgrade `5` retaliation damage/sound remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 8.2.32 Golem target range checkpoint — 2026-05-15

Статус: reference home/range target validation restored; runtime combat evidence remains open.

Что сделано:

- Restored the reference `isValidTarget(...)` home-distance check before core-specific target rules.
- Ranged and melee golem AI now share the same range rejection when validating existing targets, not just when initially acquiring them.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation of target drop behavior outside golem home/range remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 8.2.33 Golem animal target filters checkpoint — 2026-05-15

Статус: reference animal/butcher target exclusions restored; runtime combat evidence remains open.

Что сделано:

- Restored the reference animal target filter shared by normal animal-targeting golems and butcher core target validation.
- Animal-targeting golems now reject hostile `IMob` entities, tamed `EntityTameable` animals, and `EntityGolem` targets.
- Butcher core now also rejects child `EntityAnimal` targets, matching the original core `9` behavior.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation of animal/butcher target choice around tamed animals, child animals, and golems remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 8.2.34 Golem butcher target acquisition checkpoint — 2026-05-15

Статус: reference butcher acquisition baseline restored; runtime culling evidence remains open.

Что сделано:

- Replaced the `AINearestButcherTarget.shouldExecute()` stub with the original acquisition rule.
- Butcher golems now scan within golem range, sort valid candidates by oldest entity age, and select the oldest valid target only when more than two valid same-type targets are nearby.
- Restored `AIOldestAttackableTargetSorter` to sort by `ticksExisted` descending instead of distance, matching its original butcher-task role.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation of butcher culling threshold, same-type counting, and oldest-target selection remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 8.2.35 Golem item pickup delay checkpoint — 2026-05-15

Статус: reference item pickup delay gate restored; runtime pickup evidence remains open.

Что сделано:

- Restored `AIItemPickup` to gate candidate `EntityItem`s by the original pickup-delay threshold instead of permanently skipping items that have a thrower name.
- Adapted the 1.7.10 public delay-field check to Forge 1.12.2 by reading `EntityItem.pickupDelay`/`field_145804_b` through Forge `ReflectionHelper`, with `cannotPickup()` as a fallback if reflection is unavailable.
- Restored the original golem item-pickup pop sound pitch multiplier.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation that golems pick up player-thrown items after the reference delay window remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 8.2.36 Golem essentia jar destination checkpoint — 2026-05-15

Статус: reference nearest-destination selection restored for essentia emptying; runtime essentia evidence remains open.

Что сделано:

- Restored `GolemHelper.findJarWithRoom(...)` to rebuild the connected-jar cache per search instead of allowing stale `jarlist` state to affect later searches.
- Preserved the original priority tiers: suction-capable non-jar transports plus matching labeled non-full jars first, then progressively looser jar categories.
- Restored the reference nearest-candidate choice inside the selected tier, including the distance penalty for void jars.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation of essentia emptying destination choice across labeled jars, unlabeled jars, void jars, reservoirs, and transports remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 8.2.37 Golem liquid target tank checkpoint — 2026-05-15

Статус: reference home-tank missing-fluid discovery restored; runtime fluid evidence remains open.

Что сделано:

- Restored `GolemHelper.getMissingLiquids(...)` to inspect the home-adjacent target fluid handler, matching the original flow that decides what the golem should fetch or empty into.
- Adapted the original `IFluidHandler.canFill(...)` and fluid-container filter to Forge 1.12.2 capabilities with simulated `IFluidHandler.fill(...)` and `FluidUtil.getFluidContained(...)`.
- Preserved the reference constraint that an already-carried fluid restricts candidates to the same fluid.

Проверки:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — не дошел до jar inspection: отсутствует wrapper-ожидаемый MCP mapping cache `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — timeout before ready state на уже задокументированном pre-Forge/log4j этапе; `run/crash-reports/` не существует, and the configured crash-marker scan found no matches.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime confirmation of liquid gather/empty behavior with Forge fluid handlers and filled-container filters remains unavailable while smoke-server is blocked before ready state and manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 8.2.38 Cultist hostile sound inheritance checkpoint — 2026-05-16

Статус: server-visible sound contract corrected; runtime combat sound evidence remains open.

Что сделано:

- Removed explicit `null` overrides for `EntityCultist.getAmbientSound()`, `getHurtSound(...)`, and `getDeathSound()`.
- Restored base-class hostile sound inheritance for Cultist mobs, matching the reference shape where `EntityCultist` does not override these sound methods.
- Added `EntityCultistSoundContractTest` to enforce that `EntityCultist` does not regress to explicit `null` sound overrides.
- Corrected Stage 6 drop/sound notes that previously treated base Cultist silence as intentional.

Проверки:

- `./scripts/dev.sh test` — passed (`20/20`).
- `./scripts/dev.sh validate --smoke` — passed (`6/6`), including `smoke-server` ready state.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime combat/drop sound scenarios for Cultist variants, Pech, and bosses remain open in the Stage 6 matrix.
- Taint Swarm ambient silence remains reference-consistent and unchanged.

### 8.2.39 Stage 6 sound resource static coverage checkpoint — 2026-05-16

Статус: non-GUI sound registration/resource baseline enforced; runtime playback evidence remains open.

Что сделано:

- Added `TCSoundsStaticCoverageTest` to statically compare:
  - declared `sound(\"...\")` keys in `TCSounds`,
  - top-level event keys in `assets/thaumcraft/sounds.json`,
  - bundled `.ogg` files under `assets/thaumcraft/sounds/`.
- The test fails if a sound key is missing in `sounds.json`, present only in `sounds.json` but not in code, or has no matching base/variant `.ogg` asset.

Проверки:

- `./scripts/dev.sh test` — passed (`21/21`).
- `./scripts/dev.sh validate` — passed (`5/5`).
- `git diff --check` — passed.

Оставшиеся ограничения:

- This is static coverage only; it does not prove in-world playback routing for every mob/boss/combat path.
- Runtime drop/sound scenarios in the Stage 6 matrix remain open.

### 8.2.40 Monster null-sound override allowlist checkpoint — 2026-05-16

Статус: static guard added against accidental null sound regressions in Stage 6 monsters/bosses.

Что сделано:

- Added `MonsterSoundNullOverrideAllowlistTest` to scan `src/main/java/thaumcraft/common/entities/monster/**` for `getAmbientSound/getHurtSound/getDeathSound` methods that explicitly `return null`.
- Enforced an explicit allowlist with only one accepted reference-compatible case: `EntityTaintSwarm#getAmbientSound`.
- This prevents regressions like the previous `EntityCultist` null-sound override from silently reappearing.

Проверки:

- `./scripts/dev.sh test` — passed (`23/23`).
- `./scripts/dev.sh validate` — passed (`5/5`).
- `git diff --check` — passed.

Оставшиеся ограничения:

- The allowlist guard is static and does not replace runtime combat/drop sound verification.
- Stage 6 runtime sound matrix scenarios are still open.

### 8.2.41 Cultist home/faction contract checkpoint — 2026-05-16

Статус: server-visible `EntityCultist` home/faction baseline aligned with reference semantics.

Что сделано:

- Replaced ad-hoc `homeX/homeY/homeZ` fields with reference-style home persistence keys:
  - read: `HomeD/HomeX/HomeY/HomeZ` with `setHomePosAndDistance(...)`
  - write: `HomeD/HomeX/HomeY/HomeZ` only when a home is set.
- Restored base cultist faction behavior:
  - `isOnSameTeam(...)` now treats `EntityCultist` and `EntityCultistLeader` as allies.
  - `canAttackClass(...)` now refuses cultist subclasses (`EntityCultistCleric`, `EntityCultistKnight`, `EntityCultistLeader`).
- Added `EntityCultistBehaviorContractTest` static guard for these contracts.

Проверки:

- `./scripts/dev.sh test` — passed (`25/25`).
- `./scripts/dev.sh validate --smoke` — passed (`6/6`), including `smoke-server` ready state.
- `git diff --check` — passed.

Оставшиеся ограничения:

- Runtime combat matrix verification for all cultist/boss scenarios is still open in Stage 6.
- This checkpoint does not close broader Stage 6 manual scenario requirements.

### 8.2.42 Cultist Portal loot-placement static guard checkpoint — 2026-05-16

Статус: direct placeholder regression guarded; runtime portal scenario evidence remains open.

Что сделано:

- Added `EntityCultistPortalLootPlacementContractTest` to enforce that stage-0 portal reward placement stays on Thaumcraft loot blocks (`ConfigBlocks.blockLootCrate`) and does not regress to vanilla chest placeholders.
- Refreshed GAP-7 document text to match current code reality: `BlockLoot` and loot-crate placement are present; remaining blocker is runtime sequence/drop evidence.

Проверки:

- `./scripts/dev.sh test` — passed (`27/27`).
- `./scripts/dev.sh validate` — passed (`5/5`).
- `git diff --check` — passed.

Оставшиеся ограничения:

- Static checks do not prove in-world drop/state parity or full stage progression.
- `S6-BOSS-01` Cultist Portal runtime scenario remains TODO.

### 8.2.43 Monster TCSounds constant coverage checkpoint — 2026-05-16

Статус: static sound-reference integrity guard added for Stage 6 monster/boss code.

Что сделано:

- Added `MonsterSoundConstantCoverageTest` to scan `src/main/java/thaumcraft/common/entities/monster/**` for `TCSounds.*` references and verify every referenced constant exists in `TCSounds` declarations.
- This complements the existing `TCSoundsStaticCoverageTest` (code ↔ `sounds.json` ↔ `.ogg`) and catches broken monster/boss sound references before runtime.

Проверки:

- `./scripts/dev.sh test` — passed (`28/28`).
- `./scripts/dev.sh validate` — passed (`5/5`).
- `git diff --check` — passed.

Оставшиеся ограничения:

- Static checks do not prove in-world playback timing/mix for each combat path.
- Stage 6 runtime sound/drop scenario matrix remains open.

### 8.3 Minimal Stage 6 manual scenario matrix

Утвержденный минимальный формат evidence: таблица в этом документе или checkpoint report, с обязательными полями:

| ID | Area | Scenario | Setup | Steps | Expected server-visible result | Evidence | Result | Blocker/limitation |
|---|---|---|---|---|---|---|---|---|
| S6-REG-01 | Registration | All Stage 6 entities load/register | Fresh dev run dir | Run `./scripts/dev.sh validate --smoke`; scan `run/smoke-server.log` | Server reaches `Done (`; no crash markers, duplicate/missing entity registration failures, or new crash reports | `run/smoke-server.log:108` registered entities, `run/smoke-server.log:126` loaded 6 mods, `run/smoke-server.log:138` reached `Done (1.117s)!`; no crash reports found | PASS | This is a load/registration smoke only; it does not exercise spawn eggs, combat, drops, AI, GUI, or save/reload scenarios. |
| S6-PECH-01 | Pech | Spawn/type/combat variants | Dedicated/server test world | Spawn several Pechs and fight at melee/range | Melee, archer, and thaumaturge variants behave according to reference, or missing spawn setup is recorded | command/NBT/combat notes | TODO | |
| S6-PECH-02 | Pech | Taming/pickup/trade | Valued items available | Drop valued items, tame, open GUI, insert valued input, take output | Pickup/taming/trade outputs work without dupe/loss | before/after inventory notes | TODO | |
| S6-PECH-03 | Pech | Group anger | At least 3 nearby Pechs | Damage one Pech as player | Nearby Pechs aggro/reacquire same player for expected duration | combat notes/entity count | TODO | |
| S6-GOLEM-01 | Golem | Placement/configuration route | Golem/trunk items available | Place golem/trunk, apply core/upgrade/deco, use bell, open GUI | Normal item route reaches server logic and persists | item ids, GUI/NBT notes | TODO | |
| S6-GOLEM-02 | Golem AI | Core smoke sweep | Chests/crops/logs/fluids/essentia/test mobs prepared | Exercise one representative task per core 0-11 | Each core starts, completes, persists, and does not lose items | per-core notes | TODO | |
| S6-BOSS-01 | Cultist Portal | Stage progression and reward blocks | Flat arena, player within 48 blocks | Spawn portal, wait stages, fight minions/boss, kill portal | Stage progression, minions, boss spawn, collision damage, drops, death explosion, and loot crates work | stage timings/entity counts/drop notes | TODO | |
| S6-BOSS-02 | Cultist Leader | Orb/aura/drop/team | Leader plus cultists | Fight at range and kill | Ranged orb, cultist aura/team rules, and loot work | effect/drop notes | TODO | |
| S6-BOSS-03 | Eldritch Golem | Headless phase | Arena with breakable blocks | Damage near death, continue fight, save/reload during phase | One headless transition; beam/orb/melee behavior and NBT persistence work | health/timing/NBT notes | TODO | |
| S6-BOSS-04 | Eldritch Warden | Frenzy/orb/screech/warp | Arena, op player | Fight through absorption/field behavior | Field blocks, teleport-home, warp/effects, immunity rules work | block/effect/warp notes | TODO | |
| S6-PROJ-01 | Projectiles | Impact/effect/sound sweep | Representative launchers/mobs available | Fire Pech blast, golem orb, eldritch orb, primal/frost/shock/explosive projectiles | Damage, effects, lifetime, gravity, spawn data, and audible server sounds match reference | combat/log notes | TODO | |
| S6-MOB-01 | Inhabited Zombie | Crab spawn on death | Arena | Kill by player and non-player damage; save/reload spawned crab | Exactly one helmed Eldritch Crab spawns; XP/drop semantics and helm persistence match reference | entity count/NBT notes | TODO | |
| S6-DROP-01 | Drops/sounds | Representative kill/combat | Sounds enabled | Hit/kill Pech, cultists, eldritch mobs, taint mobs, golems/trunks, bosses | Drops match reference and no missing sound warnings appear | drop inventory plus log grep | TODO | |

Эта matrix не заменяет `compileJava` и `smoke-server`; она фиксирует минимальную runtime/manual evidence, без которой Stage 6 parity claim недопустим.
