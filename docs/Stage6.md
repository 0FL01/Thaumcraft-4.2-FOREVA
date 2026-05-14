# Stage 6 — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 6 покрывает серверно-видимое поведение сущностей Thaumcraft 4.2.3.5: мобы, AI, снаряды, боссы, големы, контейнеры сущностей, дропы, звуки, NBT/data watcher/data manager, spawn rules, entity registration и игровые сценарии взаимодействия. Клиентские рендереры, модели, частицы и визуальные FX остаются Phase 8 dependency, но Stage 6 не может считаться закрытой, если серверная логика только заглушена или не достигается в игре.

По PRD Stage 6 не закрыта: `docs/PRD.md:317-339` задает цель, baseline и acceptance, а `docs/PRD.md:325-333` прямо перечисляет незакрытые риски: boss special attacks, Pech trade/taming/pickup/combat, golem AI/interactions, drops/sounds runtime scenarios.

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
Current `ConfigItems` has no `itemGolemBell`, `itemGolemCore`, `itemGolemPlacer`, `itemGolemUpgrade`, `itemGolemDecoration`, or `itemTrunkSpawner` fields/registrations. The item classes are present but do not implement normal placement/subitem/metadata behavior: `ItemGolemPlacer` and `ItemTrunkSpawner` only set stack size and creative tab; current golem item classes have no on-use placement logic. Reference golem interaction code expects registered `ConfigItems.itemGolemBell`, `ConfigItems.itemGolemCore`, and `ConfigItems.itemGolemUpgrade` when applying cores/upgrades and bell workflows.

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

**Статус:** частично реализовано  
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
Reference behavior includes inactive state from the pedestal/cosmetic block under the golem, bootup sounds/status, fire resistance override, death logging, bell/deco/wheat interactions, fluid-carried NBT for fluid cores, GUI blocking while holding wand, setup inventory after upgrades, spawn data reconstruction, and item/bell constants through `ConfigItems`. Current code has a simplified `inactive = false` path in `onLivingUpdate` (`EntityGolemBase.java:101`), no fluid NBT write/read even though `fluidCarried` exists (`EntityGolemBase.java:9` and `EntityGolemBase.java:255-258` only handle essentia), no decoration/wheat/bell interaction branch (`EntityGolemBase.java:316-354`), and ranged attack sound is only a comment (`EntityGolemBase.java:596`).

**Что нужно доделать:**
Port the missing server-visible golem lifecycle and interaction details from reference, without moving renderer/FX work into Stage 6.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Update `EntityGolemBase.onLivingUpdate` to match reference inactive-home behavior using the correct current block/state equivalent.
- Add fluid-carried NBT persistence for core 5 and verify essentia core 6 persistence.
- Port decoration application/removal, wheat healing/speed behavior, bell interaction behavior, and held-wand GUI exclusion.
- Ensure `setupGolemInventory()` is called when upgrades change, not only when core is first applied.
- Replace the ranged attack sound comment with `TCSounds.GOLEMIRONSHOOT` or the correct sound event.
- Runtime scenarios: gather, empty, pickup, harvest, attack, fluid, essentia, lumber, use, butcher, sort, fish.

**Критерии приемки:**
- [ ] Golem NBT round-trip preserves core, type, advanced, colors, upgrades, markers, carried item, carried fluid, carried essentia, owner, home, and inventory.
- [ ] Golem interaction flows match reference for core/upgrades/decorations/bell/healing/GUI access.
- [ ] All golem core AI scenarios can be manually exercised on a dedicated server without stuck task loops or item loss.

**Риски / зависимости:**
Depends on GAP-1 item registration. Some visual status bytes are Phase 8, but server state changes and sounds are Stage 6.

### GAP-3: Golem AI/helper flows need parity verification and targeted fixes

**Статус:** требует проверки  
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
Current AI classes exist and `EntityGolemBase.setupGolem()` wires all reference core categories (`EntityGolemBase.java:152-213`), but there is no runtime evidence for inventory/fluid/essentia interactions. Helper logic depends on block/tile APIs and inventory semantics that changed between 1.7.10 and 1.12.2. Static inspection also shows null-return paths in `GolemHelper` and AI classes that may be valid search failures, but they have not been proven equivalent under real containers, Forge capabilities, fluids, and tile entities.

**Что нужно доделать:**
Run focused manual/server validation per golem core and fix any API mismatch found in AI/helper methods.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Audit `GolemHelper` methods used by each core against reference method intent.
- Validate chest insertion/extraction, color filters, marker range, ore dictionary/NBT/damage toggles, Forge fluid handling, essentia containers, crop/log harvesting, fishing, and combat target selection.
- Fix task priority or helper API mismatches in the specific AI classes that fail scenarios.

**Критерии приемки:**
- [ ] Each golem core completes at least one representative server scenario.
- [ ] Failed helper searches are distinguishable from broken API migrations.
- [ ] Golem config values from `Config.java` affect behavior where reference expects them.

**Риски / зависимости:**
Depends on GAP-1 and GAP-2. Some inventories/tiles/essentia containers are owned by earlier phases; if a tile API is missing, label it as dependency and keep the golem-side fix scoped.

### GAP-4: Pech spawn equipment, type setup, names, and combat task switching are incomplete

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/monster/EntityPech.java:50-97`
- `src/main/java/thaumcraft/common/entities/monster/EntityPech.java:183-200`
- `src/main/java/thaumcraft/common/entities/monster/EntityPech.java:233-273`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/monster/EntityPech.class`

**Что не совпадает:**
Reference Pech initializes random held equipment and derives Pech type during initial spawn: wand Pech gets Pech focus/vis, bow Pech becomes type 2, and equipment drop chances are adjusted. Current `EntityPech` has no `onInitialSpawn` or equipment initialization method, so `getPechType()` remains default 0 unless NBT sets it. Current `setCombatTask()` treats `getPechType() == 1 || getPechType() == 2` as ranged (`EntityPech.java:190-191`), but without spawn-time type/equipment setup normal spawned Pechs are likely melee-only. Current display-name/type localization parity is also absent.

**Что нужно доделать:**
Port reference spawn equipment/type logic to Forge 1.12.2 and update combat AI when held item changes.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Implement `onInitialSpawn` and equivalent equipment selection in `EntityPech`.
- Ensure wand Pech is given a wand/focus compatible with current wand API and Pech type 1.
- Ensure bow Pech gets Pech type 2 and ranged AI.
- Re-run `setCombatTask()` when mainhand equipment changes.
- Verify type-specific names/localization keys or document dependency on lang/content.

**Критерии приемки:**
- [ ] Naturally or command-spawned Pechs produce expected melee, archer, and thaumaturge variants.
- [ ] Type 1 Pech fires `EntityPechBlast`; type 2 Pech fires arrows; default Pech uses melee.
- [ ] Pech type persists through NBT and reload.

**Риски / зависимости:**
Depends on current wand/focus items from earlier phases. If wand NBT/focus API is incomplete, document as dependency but keep Pech type/equipment behavior intact.

### GAP-5: Pech anger/group retaliation behavior is incomplete

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/monster/EntityPech.java:224-231`
- `src/main/java/thaumcraft/common/entities/monster/EntityPech.java:206-213`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/monster/EntityPech.class`

**Что не совпадает:**
Reference `attackEntityFrom` makes nearby Pechs angry at the attacking player and sets target/charge behavior for the whole group. Current code only sets anger/tamed=false on the attacked Pech (`EntityPech.java:224-231`). Reference also suppresses target lookup while anger is zero and repeatedly reassigns the anger target during update; current `onUpdate` only decrements anger (`EntityPech.java:206-213`) and does not preserve/reacquire target while angry.

**Что нужно доделать:**
Port group anger, target assignment, charge sound/status, and non-angry target suppression.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Add `becomeAngryAt` equivalent with `setAttackTarget`, anger timer, tamed reset, and sound/status event.
- On player damage, apply anger to nearby `EntityPech` within reference range.
- Override target lookup or AI update so passive Pechs do not attack unless angry.
- Runtime scenario: attack one Pech near several Pechs and verify group aggro duration and sound.

**Критерии приемки:**
- [ ] Damaging one Pech with a player angers nearby Pechs in the reference radius.
- [ ] Pechs do not choose hostile targets when anger is zero.
- [ ] Angry Pechs keep/reacquire the attacker target until anger expires.

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

### GAP-7: Cultist Portal loot crate behavior uses a placeholder because BlockLoot is absent

**Статус:** отсутствует / реализовано неправильно  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortal.java:112-150`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortal.java:197-229`
- absent file: `src/main/java/thaumcraft/common/blocks/BlockLoot.java`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/monster/boss/EntityCultistPortal.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockLoot.class`

**Что не совпадает:**
Reference portal places Thaumcraft loot blocks/crates during the stage-0 setup. Current code explicitly uses vanilla chest as placeholder (`EntityCultistPortal.java:143-145`) because `BlockLoot` is not ported. This is server-visible boss reward behavior, not rendering. Banner facing is also a TODO (`EntityCultistPortal.java:124-127`), but the loot crate placeholder is the critical parity gap.

**Что нужно доделать:**
Port or provide the correct current equivalent of `BlockLoot` and update Cultist Portal setup to place it with correct metadata/state and loot behavior.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Add/port `BlockLoot` and any tile/drop logic it requires, or map to an existing ported loot block with identical behavior.
- Register the block/item with stable names.
- Replace vanilla chest placeholder in `EntityCultistPortal` with Thaumcraft loot block placement.
- Verify portal stage setup places banners and loot crates at reference positions and rewards.

**Критерии приемки:**
- [ ] Cultist Portal stage 0 places Thaumcraft loot blocks, not vanilla chest placeholders.
- [ ] Loot crate metadata/state and drops match reference behavior.
- [ ] Portal minion and boss spawn sequence still runs after loot placement.

**Риски / зависимости:**
Depends on block/content registration. This is a direct Stage 6 blocker because it changes boss server rewards.

### GAP-8: Eldritch Golem special server behavior is incomplete and unvalidated

**Статус:** частично реализовано / требует проверки  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java:27-39`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java:63-84`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java:101-120`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java:128-181`

**Референс:**
- `thaumcraft_src/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.class`

**Что не совпадает:**
Current code ports headless transition, beam charge, melee knockback, and ranged orb baseline. Reference also sets a spawn timer on initial spawn/headless transition, heals while spawn timer is active, emits status 18/19, plays golem walk/throw sounds, breaks low-hardness blocks while moving, and breaks `BlockLoot` server-side when walking over it. Current implementation has no `spawnTimer` use, no movement block-breaking logic, no `BlockLoot` interaction, no footstep override, and only handles status byte 4 (`EntityEldritchGolem.java:183-191`).

**Что нужно доделать:**
Port server-visible movement/block/spawn-timer behavior and validate headless beam combat in game.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Implement initial spawn/headless spawn timer equivalent in current `EntityThaumcraftBoss` model.
- Port low-hardness block breaking and `BlockLoot` interaction if `BlockLoot` is restored by GAP-7.
- Add footstep/attack sounds with current sound events.
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
Core damage/effect logic for `EntityGolemOrb`, `EntityEldritchOrb`, and `EntityPechBlast` is present. Current `EntityGolemOrb.onImpact` replaces reference `thaumcraft:shock` sound and burst with a client-side `playEvent` only (`EntityGolemOrb.java:60-62`), so dedicated server impact sound parity is missing. Current `EntityEldritchOrb.onImpact` sets dead immediately after setting `ticksExisted = 100` (`EntityEldritchOrb.java:50-53`), while reference plays fizz/status burst and allows status byte handling. `EntityPechBlast` has no explosion/status sound/FX hook; visuals are Phase 8, but impact sound/status should be classified and ported if server-visible.

**Что нужно доделать:**
Audit all projectile classes against reference and restore server sounds/status events where they are gameplay-visible or audible.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- For each projectile, compare gravity, lifetime, constructors, spawn data, damage source, potion effects, redirect/attack handling, and impact sound.
- Replace generic `playEvent` placeholders with `TCSounds` where reference used Thaumcraft sounds.
- Keep particle-only proxy calls deferred to Phase 8, but preserve status bytes if later client code depends on them.
- Runtime scenario: fire Pech blast, eldritch orb, golem orb, primal/frost/shock/explosive projectiles and verify server damage/effects/sounds.

**Критерии приемки:**
- [ ] Projectile damage, potion effects, lifetime, gravity, and homing/redirect behavior match reference.
- [ ] Server-side impact sounds are audible on dedicated server for reference-audible projectiles.
- [ ] Projectile spawn data works across server/client without class cast or missing entity errors.

**Риски / зависимости:**
Client particles/renderers are Phase 8. Sounds depend on `TCSounds` entries and assets already present in `sounds.json`.

### GAP-10: Boss special attacks and phase flows are not runtime-validated

**Статус:** требует проверки  
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
Cultist Leader, Eldritch Warden, and Cultist Portal have substantial server baselines, but acceptance requires runtime evidence. Warden field frenzy, teleport-home, absorption regeneration, warp application, cultist targeting, eldritch field block placement, and portal minion/boss sequence are all behavior-sensitive. Current `EntityThaumcraftBoss` still has TODOs for enrage/scaling (`src/main/java/thaumcraft/common/entities/monster/boss/EntityThaumcraftBoss.java:29-37`), which may be intended boss baseline behavior depending on reference champion/boss modifiers.

**Что нужно доделать:**
Perform targeted runtime validation for every boss phase and fix behavior mismatches found. Resolve or explicitly prove non-applicability of boss TODOs.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Compare `EntityThaumcraftBoss` reference for spawn timer, boss health/name/champion/enrage mechanics.
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
Pech death loot has a baseline and uses mana beans/coins/knowledge fragments (`EntityPech.java:390-413`), but runtime drops are unverified. Cultist Leader drops a loot bag (`EntityCultistLeader.java:162-166`). Eldritch Guardian drops essences/equipment (`EntityEldritchGuardian.java:162-184`). Several mobs return null sounds, including `EntityCultist` ambient/hurt/death and `EntityTaintSwarm` ambient. Null may be intentional for some mobs, but Stage 6 scope includes sounds and the current state has no documented reference comparison or runtime verification.

**Что нужно доделать:**
Audit all Stage 6 drops/sounds against reference and run representative kill/combat scenarios.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Build a table of each monster/boss `getAmbientSound`, `getHurtSound`, `getDeathSound`, `dropFewItems`, `dropEquipment`, and XP behavior.
- Compare null sounds with reference before changing.
- Verify all referenced `TCSounds` names exist in `sounds.json` and asset files under `src/main/resources/assets/thaumcraft/sounds/` or copied from `thaumcraft_src/assets/`.
- Runtime scenario: kill representative Pech, cultists, eldritch mobs, taint mobs, golems/trunks, and bosses with/without looting.

**Критерии приемки:**
- [ ] Drop outputs match reference for representative mobs and bosses.
- [ ] All non-null sound events resolve and play without missing sound warnings.
- [ ] Any intentionally silent mob sound is documented with reference evidence.

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
All core current entity classes are registered with Forge 1.12 `EntityEntryBuilder`, but names are current lower-snake registry names like `thaumcraft:pech`, `thaumcraft:eldritch_golem`, and `thaumcraft:taintacle_tiny` (`ConfigEntities.java:91-126`). Reference 1.7.10 used its own entity id/spawn egg item infrastructure, including `ItemSpawnerEgg`, which is absent in current source. Forge 1.12 eggs may replace some behavior, but registry identity and spawn egg availability need explicit compatibility review because PRD treats registry names and ids as public contracts (`docs/PRD.md:61-69`).

**Что нужно доделать:**
Confirm stable 1.12 registry names and decide whether the reference `ItemSpawnerEgg` behavior must be ported or is intentionally replaced by Forge entity eggs.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Decompile/reference original entity registration and spawn egg mappings.
- Produce a mapping table: reference entity name/id/egg colors to current registry name/egg colors.
- Add missing custom spawn egg item only if Forge eggs do not preserve required Thaumcraft behavior.
- Run registry smoke to verify all entity entries register once and spawn eggs spawn correct entity classes.

**Критерии приемки:**
- [ ] Every Stage 6 entity has documented current registry name and spawn path.
- [ ] Spawn egg behavior is either reference-compatible or explicitly replaced with Forge-compatible behavior.
- [ ] No duplicate/missing entity registration warnings occur during runtime smoke.

**Риски / зависимости:**
Save compatibility from 1.7 entity ids to 1.12 registry names may need broader migration policy. Keep this gap scoped to Stage 6 entity identity and spawnability.

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
Current implementation spawns an `EntityEldritchCrab` with helm at `deathTime == 0` and suppresses normal zombie drops (`EntityInhabitedZombie.java:18-45`). This is the baseline mentioned by PRD, but it is sensitive to 1.12 death update ordering, XP/drop timing, and crab NBT/data manager setup. It has not been runtime-validated.

**Что нужно доделать:**
Validate death update ordering and spawned crab behavior on a dedicated server.

**Как доделать:**
- files/classes/methods/registrations/resources/scenarios
- Spawn Inhabited Zombie, kill it by player and non-player damage.
- Verify exactly one helm crab spawns at the correct position.
- Verify XP rules and no duplicate zombie drops.
- Save/reload spawned crab and verify helm state persists.

**Критерии приемки:**
- [ ] Player kill spawns one helmed Eldritch Crab and expected XP.
- [ ] Non-player kill follows reference XP/drop semantics.
- [ ] Crab helm state survives save/load.

**Риски / зависимости:**
Client crab renderer is Phase 8; server entity state and spawn count are Stage 6.

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
- [ ] Port Pech spawn equipment/type setup, group anger, target reacquisition, and trade output generation.
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
- Legacy 1.7.10 names вроде `Thaumcraft.BrainyZombie` считаются reference/source names и должны быть отражены в compatibility mapping, но не заменяют current registry names.
- Текущий порядок и набор Stage 6 entity registrations в `ConfigEntities` нужно считать стабильным: новые entries добавлять append-only, без reorder, если нет явно задокументированной migration reason.
- Старый custom `ItemSpawnerEgg` не портируется как blocker Stage 6. Forge 1.12 spawn eggs являются основной реализацией spawn egg behavior.
- Legacy `ItemSpawnerEgg` можно вернуть позже только как отдельный compatibility checkpoint, если появится конкретное требование от старых карт/addons/save migration.

Практическое следствие для GAP-12: закрывать через mapping table `legacy entity name/id/egg colors -> current registry name/egg colors`, registry smoke, и явное документирование замены старого egg item на Forge eggs.

### 8.2 `BlockLoot` ownership and Stage 6 closure policy

Утвержденная policy для Stage 6:

- `BlockLoot` может принадлежать content/block checkpoint по ownership, но для Stage 6 это прямой blocker, потому что `EntityCultistPortal` сейчас меняет server-visible boss reward behavior vanilla chest placeholder'ом.
- Stage 6 нельзя считать закрытой, пока Cultist Portal stage 0 ставит vanilla chests вместо reference-compatible Thaumcraft loot crates.
- Минимально приемлемое закрытие: port `BlockLoot`, `BlockLootItem`, `blockLootUrn`, `blockLootCrate`, регистрацию block/item, loot generation path, и замену placeholder в `EntityCultistPortal`.
- Safe shell без реального loot behavior не считается parity closure. Если полноценные loot pools зависят от Stage 9/content registration, это нужно явно отметить как dependency, но не скрывать пустым блоком.

Практическое следствие для GAP-7: делать отдельный scoped checkpoint `BlockLoot + Cultist Portal replacement`; после него запускать `compileJava`, `smoke-server`, и manual Cultist Portal scenario.

### 8.3 Minimal Stage 6 manual scenario matrix

Утвержденный минимальный формат evidence: таблица в этом документе или checkpoint report, с обязательными полями:

| ID | Area | Scenario | Setup | Steps | Expected server-visible result | Evidence | Result | Blocker/limitation |
|---|---|---|---|---|---|---|---|---|
| S6-REG-01 | Registration | All Stage 6 entities load/register | Fresh dev run dir | Run `./scripts/dev.sh smoke-server` | Server reaches `Done (`; no crash markers, duplicate/missing entity registration failures, or new crash reports | `run/smoke-server.log` line references | TODO | |
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
