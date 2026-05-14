# Stage 3 — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 3 закрывает core-системы Thaumcraft 4.2.3.5 на Forge 1.12.2: аспекты, object/entity scan aspects, aura/vis-сеть, хранение и расход vis в жезлах и baubles, research/scans/warp player state, potion/enchantment baselines и capability-backed persistence/sync. По PRD цель фазы сформулирована как работоспособность `aspects, aura/vis, wands, research, scan state, warp, potions, enchantments, and player capabilities` (`docs/PRD.md:238-255`), а acceptance требует сохранить уже имеющиеся baseline-реализации и закрыть или явно отложить оставшиеся gaps (`docs/PRD.md:257-260`).

Stage 3 нельзя считать завершенной только по компиляции: PRD прямо указывает, что Phase 3 не закрыта и не validated (`docs/PRD.md:244-247`).

## 2. Scope фазы

В scope Stage 3 входят следующие механики, классы и интеграции текущего порта:

- Aspect API и object tag pipeline: `src/main/java/thaumcraft/api/aspects/Aspect.java:10-167`, `src/main/java/thaumcraft/api/aspects/AspectList.java:11-218`, `src/main/java/thaumcraft/api/ThaumcraftApi.java:48-58`, `src/main/java/thaumcraft/api/ThaumcraftApi.java:207-260`, `src/main/java/thaumcraft/common/config/ConfigAspects.java:10-191`, `src/main/java/thaumcraft/common/lib/InternalMethodHandler.java:53-75`.
- Aura/vis nodes and vis relay network: `src/main/java/thaumcraft/api/visnet/TileVisNode.java:15-192`, `src/main/java/thaumcraft/api/visnet/VisNetHandler.java:20-229`, `src/main/java/thaumcraft/common/tiles/TileNode.java:14-208`, tile registration in `src/main/java/thaumcraft/common/Thaumcraft.java:144-171`.
- Wand rods/caps/foci, centi-vis storage, discounts, inventory consumption: `src/main/java/thaumcraft/common/Thaumcraft.java:337-357`, `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:37-403`, `src/main/java/thaumcraft/common/items/wands/WandManager.java:22-230`, item registration in `src/main/java/thaumcraft/common/config/ConfigItems.java:47-66`, `src/main/java/thaumcraft/common/config/ConfigItems.java:203-260`.
- Baubles vis and runic integrations: `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:20-170`, `src/main/java/thaumcraft/common/items/baubles/ItemRingRunic.java:12-73`, `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:37-360`, Baubles dependency in `build.gradle:42-44`.
- Player knowledge capability, NBT persistence, clone/join sync: `src/main/java/thaumcraft/common/lib/capabilities/IPlayerKnowledge.java:10-96`, `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeCapability.java:14-291`, `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeProvider.java:16-81`, capability registration in `src/main/java/thaumcraft/common/Thaumcraft.java:95-97`, attach/sync/clone in `src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java:53-115`.
- Research state, research completion, research notes, scan state: `src/main/java/thaumcraft/common/lib/research/PlayerKnowledge.java:9-59`, `src/main/java/thaumcraft/common/lib/research/ResearchManager.java:19-145`, `src/main/java/thaumcraft/common/lib/research/ScanManager.java:18-144`, `src/main/java/thaumcraft/common/items/ItemResearchNotes.java:15-49`, `src/main/java/thaumcraft/common/config/ConfigResearch.java:3-8`.
- Stage 3 packets and sync semantics: `src/main/java/thaumcraft/common/lib/network/PacketHandler.java:55-96`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncAspects.java`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncResearch.java:18-68`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncScannedItems.java`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncScannedEntities.java`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncScannedPhenomena.java`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncWarp.java:14-61`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketScannedToServer.java:18-98`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketAspectPool.java:7-37`, `src/main/java/thaumcraft/common/lib/network/playerdata/PacketAspectDiscovery.java:5-7`.
- Warp, potions, enchantments: `src/main/java/thaumcraft/common/lib/WarpEvents.java:44-343`, potion creation in `src/main/java/thaumcraft/common/config/Config.java:201-228`, potion registry in `src/main/java/thaumcraft/common/Thaumcraft.java:263-275`, enchantment creation/registry in `src/main/java/thaumcraft/common/Thaumcraft.java:278-287`, `src/main/java/thaumcraft/common/Thaumcraft.java:359-367`, and enchantment classes under `src/main/java/thaumcraft/common/lib/enchantment/`.

Сценарии, которые должны работать после closure Stage 3:

- Player knowledge persists across login, logout, death clone and server restart for research, discovered aspects, aspect pools, scans, warp and runic charge.
- Fresh-world player knowledge persists through capability-backed storage and authoritative sync.
- Research completion unlocks state, applies warp from research, fires callbacks, saves, syncs to client, and works for online/offline username lookups.
- Thaumometer scan validates prerequisites, records item/entity/phenomena scan keys compatible with the reference, grants discovered aspects/aspect pool, and syncs client state.
- Wands store vis in centi-vis, respect rod/cap/focus NBT keys, discounts, Frugal/Potency/Treasure upgrades, crafting vs non-crafting cost semantics, and consume vis from wand/baubles/inventory consistently.
- Vis Amulet stores/fills/drains primal centi-vis, charges held wand, charges from nearby relay/network, obeys research gating, and syncs NBT.
- Runic shielding computes charge from armor/baubles, consumes vis using configured costs, syncs charge, absorbs damage, handles ring/amulet/girdle upgrades, and persists relevant state.
- Warp events apply original-style potion/entity/research side effects and sync warp/notifications.
- Potions and enchantments are registered with stable names and runtime behavior matching the reference.

## 3. Источники сравнения

- Product requirements: `docs/PRD.md:57-69`, `docs/PRD.md:73-115`, `docs/PRD.md:238-260`.
- Build/dependency context: `build.gradle:24-29`, `build.gradle:42-44`; Docker Java/CFR environment at `Dockerfile:61-69`.
- Current implementation: `src/main/java/thaumcraft/api/**`, `src/main/java/thaumcraft/common/**`, especially files listed in section 2.
- Reference implementation: class files under `thaumcraft_src/thaumcraft/**` and original jar `Thaumcraft-1.7.10-4.2.3.5.jar`; the reference source tree is class-only for Stage 3, so comparison used `javap` and CFR output without modifying reference files.
- Reference signatures inspected with `javap -classpath thaumcraft_src -private ...` for `thaumcraft.common.lib.research.PlayerKnowledge`, `ResearchManager`, `ScanManager`, `thaumcraft.common.items.wands.ItemWandCasting`, `ItemAmuletVis`, `ItemRingRunic`, `EnchantmentFrugal`, `PotionWarpWard`, `thaumcraft.api.visnet.VisNetHandler`.
- Reference persistence and scan behavior inspected with CFR for `thaumcraft_src/thaumcraft/common/lib/research/ResearchManager.class`, `thaumcraft_src/thaumcraft/common/lib/research/ScanManager.class`, `thaumcraft_src/thaumcraft/common/items/baubles/ItemAmuletVis.class`, and `thaumcraft_src/thaumcraft/common/items/baubles/ItemRingRunic.class`.

Lightweight commands run during analysis:

- `git status --short`: found unrelated untracked `docs/Stage1.md` and `docs/Stage2.md`; they were not edited.
- `find src/main/java/thaumcraft ...`: enumerated current Stage 3 files.
- `find thaumcraft_src/thaumcraft ...`: enumerated reference Stage 3 class files.
- `javap -classpath thaumcraft_src -private ...`: inspected reference method/field signatures.
- `cfr ... | rg ...`: inspected reference persistence, scan and bauble behavior snippets.

No build or runtime validation was run; this task is a static documentation-only gap analysis.

## 4. Текущее состояние Stage 3

Baselines present:

- Aspect definitions and `AspectList` exist and preserve many original NBT keys such as `Aspects`, `key`, `amount` (`src/main/java/thaumcraft/api/aspects/Aspect.java:17-66`, `src/main/java/thaumcraft/api/aspects/AspectList.java:175-217`).
- Object tag registration exists for a subset of vanilla blocks/items/ore dictionary entries (`src/main/java/thaumcraft/common/config/ConfigAspects.java:12-191`).
- Wand centi-vis storage exists with NBT prefix `vis_`, rod/cap/focus tags `rod`, `cap`, `focus`, capacity scaling and basic discount logic (`src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:39-151`).
- Vis Amulet stores primal centi-vis and transfers up to 5 centi-vis/tick interval into the held wand every 5 ticks (`src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:48-116`, `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:123-137`).
- Player knowledge capability is registered and attached to players (`src/main/java/thaumcraft/common/Thaumcraft.java:95-97`, `src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java:60-67`).
- Join/clone sync baselines exist for aspects, research, scans and warp (`src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java:71-115`).
- Server potion and enchantment registry baselines exist (`src/main/java/thaumcraft/common/config/Config.java:201-228`, `src/main/java/thaumcraft/common/Thaumcraft.java:263-287`, `src/main/java/thaumcraft/common/Thaumcraft.java:361-367`).
- Thaumometer has a server-side hook that calls `ScanManager.scanEntity` or `ScanManager.scanItem` (`src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java:35-58`).
- Frugal applicability baseline exists for focus items and books (`src/main/java/thaumcraft/common/lib/enchantment/EnchantmentFrugal.java:42-52`).

Major mismatches with the reference remain:

- Reference `PlayerKnowledge` stores per-username maps for completed research, discovered aspects, scanned objects/entities/phenomena, warp counts and warp values; current capability stores a single attached player's sets/ints only and lacks aspect pools (`javap` reference `thaumcraft.common.lib.research.PlayerKnowledge`; current `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeCapability.java:18-33`).
- Reference `ResearchManager` exposes full research note, clue, completion, player data persistence, NBT tags and scan completion API; current `ResearchManager` only implements online/cached lookup and simple addResearch (`javap` reference `ResearchManager`; current `src/main/java/thaumcraft/common/lib/research/ResearchManager.java:19-145`).
- Reference scan uses hashed object/entity/phenomena keys with `@`/`#` prefixes, prerequisite validation, grouped object tags, aspect pool syncing and node phenomena; current scan directly stores registry names and simply discovers all aspects (`javap`/CFR reference `ScanManager`; current `src/main/java/thaumcraft/common/lib/research/ScanManager.java:24-144`).
- Reference Vis Amulet charges from held wand and nearby `TileVisRelay`; current only charges held mainhand wand and lacks relay pull (`CFR reference `ItemAmuletVis.onWornTick`; current `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:123-137`).
- Reference runic system is config-sensitive and syncs charge/FX; current runic handler uses hardcoded constants and contains commented packet/FX/sound send calls (`src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:48-52`, `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:107-113`, `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:146-148`, `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:212-269`).
- Current Stage 3 contains explicit Stage 3 stubs in addon-facing internal methods (`src/main/java/thaumcraft/common/lib/InternalMethodHandler.java:65-75`).

Because blocker/high gaps remain, Stage 3 is not complete.

## 5. Gap list

### GAP-1: Capability-backed player knowledge persistence is incomplete

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeCapability.java:228-291`
- `src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java:71-115`
- `src/main/java/thaumcraft/common/lib/research/ResearchManager.java:84-119`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/research/ResearchManager.class`
- CFR reference constants/methods: `RESEARCH_TAG = "THAUMCRAFT.RESEARCH"`, `ASPECT_TAG = "THAUMCRAFT.ASPECTS"`, `SCANNED_OBJ_TAG = "THAUMCRAFT.SCAN.OBJECTS"`, `SCANNED_ENT_TAG = "THAUMCRAFT.SCAN.ENTITIES"`, `SCANNED_PHE_TAG = "THAUMCRAFT.SCAN.PHENOMENA"`; `loadPlayerData`, `savePlayerData`, `loadResearchNBT`, `saveResearchNBT`, `loadAspectNBT`, `saveAspectNBT`, `loadScannedNBT`, `saveScannedNBT`.

**Что не совпадает:**

Current state persists Forge capability NBT with keys `warpPerm`, `warpSticky`, `warpTemp`, `warpCounter`, `discoveredAspects`, `scannedEntities`, `scannedItems`, `scannedPhenomena`, `researchComplete` (`src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeCapability.java:230-238`). The active project target is fresh worlds, so external 1.7.10 player data import is out of scope. The remaining Stage 3 problem is making capability persistence authoritative, complete and validated for fresh-world research/aspect/scan/warp state.

**Что нужно доделать:**

Implement a 1.12.2 capability-backed save/load path that preserves fresh-world research, aspects, aspect pools, scans, warp and runic shielding without external player-data bridges.

**Как доделать:**
- Add Stage 3-compatible methods to `ResearchManager` or capability helpers for authoritative load/save, mutation scheduling and cache refresh.
- Map Stage 3 NBT keys into `IPlayerKnowledge` fields and add missing aspect pool storage from GAP-2.
- Hook load on player login/server player data availability and save on research/aspect/scan/warp mutation.
- Files/classes: `ResearchManager`, `PlayerKnowledgeCapability`, `IPlayerKnowledge`, `EventHandlerEntity`, possibly `CommonProxy` if central access is needed.
- Scenarios: first login in a fresh world, server restart, player death clone, relog, and username lookup for current-server data.

**Критерии приемки:**
- [ ] Fresh-world research/aspect/scan/warp data loads into the 1.12.2 capability after relog/restart.
- [ ] No external player-data bridge is required for Stage 3 closure.
- [ ] Saving preserves research, aspects, aspect pools, scans, warp and runic shielding using documented capability keys.
- [ ] Server restart does not lose Stage 3 player knowledge.

**Риски / зависимости:**

High save-data risk for current fresh-world player state. Requires careful mapping between username-based helper APIs and Forge 1.12.2 player UUID/profile storage. This is a Stage 3 blocker; it should not be hidden behind a later phase.

### GAP-2: PlayerKnowledge не хранит aspect pools и не совпадает с reference API семантически

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/capabilities/IPlayerKnowledge.java:47-95`
- `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeCapability.java:24-33`
- `src/main/java/thaumcraft/common/lib/research/PlayerKnowledge.java:9-59`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/research/PlayerKnowledge.class`
- Reference signatures include `getAspectPoolFor`, `addAspectPool`, `setAspectPool`, `hasDiscoveredParentAspects`, `addDiscoveredPrimalAspects`, username maps for `researchCompleted`, `aspectsDiscovered`, `objectsScanned`, `entitiesScanned`, `phenomenaScanned`, `warpCount`, `warp`, `warpSticky`, `warpTemp`.

**Что не совпадает:**

Current capability tracks only discovered aspect tags as a set; it cannot represent reference aspect pool amounts (`short amount` per aspect), parent aspect discovery checks, primal bootstrap, or username-indexed offline data. `PacketAspectPool` exists but is payload-only and unused (`src/main/java/thaumcraft/common/lib/network/playerdata/PacketAspectPool.java:7-37`). WarpEvents has a comment that aspect pool sync is not available (`src/main/java/thaumcraft/common/lib/WarpEvents.java:221-233`).

**Что нужно доделать:**

Extend player knowledge with aspect pool amounts and reference-compatible helper methods, while keeping the Forge capability as the storage backend.

**Как доделать:**
- Add aspect pool map to `IPlayerKnowledge`/`PlayerKnowledgeCapability`: `getAspectPoolFor`, `addAspectPool`, `setAspectPool`.
- Add methods for `hasDiscoveredParentAspects` and `addDiscoveredPrimalAspects` to `PlayerKnowledge` or equivalent helpers used by scan/research.
- Serialize aspect pool using documented capability NBT entries with `key` and `amount`.
- Wire `PacketAspectPool` and `PacketAspectDiscovery` to update client capability.
- Files/classes: `IPlayerKnowledge`, `PlayerKnowledgeCapability`, `PlayerKnowledge`, `ResearchManager`, `PacketAspectPool`, `PacketAspectDiscovery`, `WarpEvents`, `ScanManager`.

**Критерии приемки:**
- [ ] Aspect pool amounts survive logout/restart and are distinct from discovered aspect booleans.
- [ ] Scan and warp research grants can increment aspect pool and sync totals to client.
- [ ] Parent-aspect prerequisite checks match the reference for compound aspects.
- [ ] Existing discovered aspects still sync through `PacketSyncAspects`.

**Риски / зависимости:**

Depends on GAP-1 persistence and GAP-5 scan completion. Public addon API should not be changed unless unavoidable.

### GAP-3: ResearchManager missing reference research-note, clue, completion and requisite behavior

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/research/ResearchManager.java:19-145`
- `src/main/java/thaumcraft/common/items/ItemResearchNotes.java:41-46`
- `src/main/java/thaumcraft/common/config/ConfigResearch.java:3-8`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/research/ResearchManager.class`
- `thaumcraft_src/thaumcraft/common/lib/research/ResearchNoteData.class` exists in reference and is absent in current source.

**Что не совпадает:**

Reference `ResearchManager` includes `createClue`, `createResearchNoteForPlayer`, `findHiddenResearch`, `findMatchingResearch`, `getResearchSlot`, ink consumption, note hex validation, `createNote`, `getData`, `updateData`, requisites, aspect combination, `completeResearchUnsaved`, `completeAspectUnsaved`, scanned completion and persistence. Current `ResearchManager` only checks/adds completed research, syncs one packet and fires a callback (`src/main/java/thaumcraft/common/lib/research/ResearchManager.java:27-82`). `ItemResearchNotes` shrinks the stack with `// Grant research from notes - TBD` and no actual research handling (`src/main/java/thaumcraft/common/items/ItemResearchNotes.java:41-46`). `ResearchNoteData.java` is absent in current source; this matters because reference note solving and NBT state use it.

**Что нужно доделать:**

Port the Stage 3 research-state backend enough for original research notes, clue generation, completion, requisites, aspect combination and scan/research unlock semantics to work.

**Как доделать:**
- Add/port `src/main/java/thaumcraft/common/lib/research/ResearchNoteData.java` from reference behavior.
- Implement reference-compatible `ResearchManager` methods for notes, completion, requisites, aspect reduction/combination and scanned completion.
- Replace `ItemResearchNotes` placeholder with actual note read/use behavior.
- Ensure `addResearch` applies reference warp from `ThaumcraftApi.getWarp(key)` like reference `completeResearch` does.
- Files/classes: `ResearchManager`, new `ResearchNoteData`, `ItemResearchNotes`, `PlayerKnowledgeCapability`, related packets.

**Критерии приемки:**
- [ ] Research notes carry and update reference-compatible NBT.
- [ ] Completing research saves, syncs, applies warp and triggers callbacks exactly once.
- [ ] Requisite checks reject unavailable research and allow valid research.
- [ ] `ItemResearchNotes` no longer contains TBD behavior in Stage 3 paths.

**Риски / зависимости:**

Dependency: actual research content registration is noted as later content work (`ConfigResearch.init()` is a Phase 9 placeholder at `src/main/java/thaumcraft/common/config/ConfigResearch.java:5-7`). Stage 3 can still close the backend, but full gameplay coverage depends on registered research entries.

### GAP-4: Offline username lookup is cache-only and loses reference behavior

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/research/ResearchManager.java:21-23`
- `src/main/java/thaumcraft/common/lib/research/ResearchManager.java:92-144`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/research/ResearchManager.class`
- `thaumcraft_src/thaumcraft/common/lib/research/PlayerKnowledge.class`

**Что не совпадает:**

Current `getResearchData(String username)` finds an online player, updates `playerDataCache`, then returns cache only (`src/main/java/thaumcraft/common/lib/research/ResearchManager.java:95-109`). For a fresh-world target, username APIs still need an authoritative current-server data source instead of returning stale or missing cache entries.

**Что нужно доделать:**

Make username-based API calls work for offline players, because addon API and research helpers use username variants (`src/main/java/thaumcraft/common/lib/InternalMethodHandler.java:31-44`, `src/main/java/thaumcraft/common/lib/research/PlayerKnowledge.java:14-35`).

**Как доделать:**
- Integrate username lookup with the GAP-1 capability persistence/cache path.
- Cache current-server data using normalized username and invalidate/update on player login/logout/save.
- Ensure `ThaumcraftApiHelper.isResearchComplete(username, key)` and `hasDiscoveredAspect(username, aspect)` work without the player being online.
- Files/classes: `ResearchManager`, `PlayerKnowledge`, `InternalMethodHandler`, capability storage/cache layer.

**Критерии приемки:**
- [ ] Username research checks work for online players.
- [ ] Username research checks work for players with existing fresh-world server data.
- [ ] Cache does not return stale data after the player logs in and changes research.

**Риски / зависимости:**

Depends on GAP-1. Must avoid changing public `thaumcraft.api.*` signatures.

### GAP-5: ScanManager scan keys, validation and aspect rewards do not match reference

**Статус:** реализовано неправильно  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/research/ScanManager.java:18-144`
- `src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java:35-58`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketScannedToServer.java:18-98`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/research/ScanManager.class`
- CFR reference methods: `generateItemHash`, `generateEntityHash`, `generateEntityAspects`, `generateNodeAspects`, `isValidScanTarget`, `hasBeenScanned`, `completeScan`, `checkAndSyncAspectKnowledge`, `validScan`, `getScanAspects`.

**Что не совпадает:**

Current item scan stores `registryName` only (`src/main/java/thaumcraft/common/lib/research/ScanManager.java:62-78`), entity scan stores entity registry name (`src/main/java/thaumcraft/common/lib/research/ScanManager.java:27-45`), and phenomena scan stores raw key (`src/main/java/thaumcraft/common/lib/research/ScanManager.java:92-106`). Reference stores hashed scan keys with `@`/`#` prefixes, grouped object tag normalization, item/entity/phenomena distinction, prerequisite validation against discovered parent aspects, aspect pool reward amounts, and node phenomena support. Current scanning grants all aspects immediately as discovered tags (`src/main/java/thaumcraft/common/lib/research/ScanManager.java:46-51`, `src/main/java/thaumcraft/common/lib/research/ScanManager.java:79-84`) and returns a `ScanResult` without completing reference scan semantics.

**Что нужно доделать:**

Port reference scan completion behavior, including scan key generation, validation, rewards and sync.

**Как доделать:**
- Implement reference-compatible `generateItemHash`, `generateEntityHash`, `generateEntityAspects`, `generateNodeAspects`, `isValidScanTarget`, `hasBeenScanned`, `completeScan`, `checkAndSyncAspectKnowledge`, `validScan`, `getScanAspects`.
- Change `ScanManager.scanItem/scanEntity/scanPhenomena` or add wrappers so server packet and Thaumometer call the same completion path.
- Store scanned object/entity/phenomena keys with prefixes compatible with reference.
- Use `ThaumcraftApi.groupedObjectTags` (`src/main/java/thaumcraft/api/ThaumcraftApi.java:55-56`) and object tag lookup.
- Sync aspects, aspect pools and scanned sets after completion.
- Files/classes: `ScanManager`, `ItemThaumometer`, `PacketScannedToServer`, player knowledge capability and scan sync packets.

**Критерии приемки:**
- [ ] Scanning the same item metadata/group twice is rejected using reference-compatible scan keys.
- [ ] Invalid scans with unknown/no aspects fail rather than marking progress.
- [ ] Valid scans discover or increment aspect knowledge according to reference amounts.
- [ ] Node phenomena scans use `NODE...` keys and record phenomena scans.

**Риски / зависимости:**

Depends on GAP-2 aspect pools and GAP-11 object tag generation. Client visual scan UX may be Phase 8, but server scan state is Stage 3.

### GAP-6: Research/scan/aspect sync packets are incomplete and may merge stale client state

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java:103-115`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncResearch.java:18-68`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketResearchComplete.java:15-52`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncWarp.java:14-61`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketAspectPool.java:7-37`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketAspectDiscovery.java:5-7`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/network/playerdata/*.class`
- Reference `ResearchManager.completeResearch`, `completeAspect`, `completeScannedObject`, `completeScannedEntity`, `completeScannedPhenomena` schedule save and send targeted packets.

**Что не совпадает:**

Current full sync sends sets on join, but packet handlers add entries without clearing old client state (`PacketSyncResearch.java:57-63`). `PacketAspectPool` has no handler and `PacketAspectDiscovery` is an empty class. Warp sync omits `warpCounter` (`PacketSyncWarp.java:14-61`), while capability persists it (`PlayerKnowledgeCapability.java:230-238`). Runic charge packet is registered but send calls are commented in `EventHandlerRunic` (`src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:107-113`, `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:146-148`).

**Что нужно доделать:**

Make Stage 3 packets authoritative and complete for join, mutation and wipe scenarios.

**Как доделать:**
- Full sync packets should replace matching client sets or be preceded by `PacketSyncWipe` semantics.
- Implement `PacketAspectPool` and `PacketAspectDiscovery` handlers.
- Include warp counter or document why server-only counter is safe.
- Re-enable runic charge sync once client packet behavior is safe.
- Add mutation sync after research/scans/aspect/warp changes, not only join.
- Files/classes: `PacketSync*`, `PacketAspectPool`, `PacketAspectDiscovery`, `PacketRunicCharge`, `ResearchManager`, `ScanManager`, `WarpEvents`, `EventHandlerEntity`, `EventHandlerRunic`.

**Критерии приемки:**
- [ ] Login sync produces exactly server state on client, without stale research/scans.
- [ ] Research, scan, aspect pool and warp mutations sync during the same session.
- [ ] Wipe/reset scenarios clear client state.
- [ ] Runic charge packet is sent when charge/max changes.

**Риски / зависимости:**

Client display of notifications/FX may depend on Phase 8, but server-to-client state correctness is Stage 3.

### GAP-7: Capability persistence/clone behavior is not validated and may miss death/original capability edge cases

**Статус:** требует проверки  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java:60-99`
- `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeProvider.java:50-79`
- `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeCapability.java:240-291`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/research/ResearchManager.class`
- `thaumcraft_src/thaumcraft/common/lib/research/PlayerKnowledge.class`

**Что не совпадает:**

Reference saves knowledge through explicit research data files and username maps. Current relies on Forge player capability serialization and copies old capability in `PlayerEvent.Clone` (`src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java:81-99`). In Forge 1.12.2, original player capabilities can require revival/invalidation handling during clone; static analysis cannot confirm this implementation survives death/end return/relog in all cases. Also `syncAllData` casts to `EntityPlayerMP` without checking type (`src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java:103-115`).

**Что нужно доделать:**

Verify and harden capability lifecycle for Stage 3 state.

**Как доделать:**
- Add safe clone handling for invalidated original caps if needed by Forge 1.12.2.
- Ensure `setPlayer` is called after deserialization/load.
- Sync after death clone and normal dimension return.
- Runtime test death, End return, relog and server restart.
- Files/classes: `EventHandlerEntity`, `PlayerKnowledgeProvider`, `PlayerKnowledgeCapability`, `ResearchManager` persistence layer.

**Критерии приемки:**
- [ ] Research/aspects/scans/warp survive player death.
- [ ] Research/aspects/scans/warp survive End return clone.
- [ ] Research/aspects/scans/warp survive logout/server restart.
- [ ] No capability cast or null capability crash during join/clone.

**Риски / зависимости:**

Depends on GAP-1 and runtime smoke/manual validation.

### GAP-8: Wand rod/cap registration uses placeholder ItemStacks and omits reference component breadth

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/Thaumcraft.java:337-357`
- `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:61-151`
- `src/main/java/thaumcraft/common/config/ConfigItems.java:203-219`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/wands/ItemWandCasting.class`
- `thaumcraft_src/thaumcraft/api/wands/WandRod.class`
- `thaumcraft_src/thaumcraft/api/wands/WandCap.class`
- `thaumcraft_src/thaumcraft/api/wands/StaffRod.class`

**Что не совпадает:**

Current registers rods/caps in `preInit` before `ConfigItems.init()`, explicitly noting ItemStacks are empty placeholders (`src/main/java/thaumcraft/common/Thaumcraft.java:339-343`). Only `wood`, `greatwood`, `silverwood` rods and six caps are registered (`src/main/java/thaumcraft/common/Thaumcraft.java:344-354`). Reference has broader rod/cap/staff behavior and `ItemWandCasting` methods such as `isStaff`, `hasRunes`, object-in-use handling, focus potency/treasure/frugal/enlarge/extend, use tick, architect blocks, harvesting and focus behavior (`javap` reference `ItemWandCasting`). Current `ItemWandCasting` lacks `isStaff`, `hasRunes`, object-in-use methods and returns `0` for treasure (`src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:219-226`).

**Что нужно доделать:**

Port reference wand component registration and missing wand behavior required by Stage 3 core systems.

**Как доделать:**
- Move/fix rod/cap registration so craft/display ItemStacks point at registered `itemWandRod`/`itemWandCap` variants.
- Add missing rods/caps/staff rods or document content dependency if item variants are not yet present.
- Implement missing `ItemWandCasting` behavior for staff/sceptre/runes, object-in-use NBT, focus upgrade getters and usage semantics.
- Verify NBT keys remain original-compatible: `rod`, `cap`, focus storage, `sceptre`, area keys.
- Files/classes: `Thaumcraft`, `ConfigItems`, `ItemWandCasting`, `ItemWandRod`, `ItemWandCap`, `WandRodPrimalOnUpdate`, `WandRod`, `WandCap`, `StaffRod`.

**Критерии приемки:**
- [ ] All Stage 3 rods/caps that exist as items have matching registered `WandRod`/`WandCap` entries with non-empty craft stacks.
- [ ] Wand capacity/cost modifiers match reference for wand/sceptre/staff cases.
- [ ] Focus upgrade getters return actual upgrades/enchantments for frugal, potency, treasure, enlarge and extend.
- [ ] Object-in-use NBT and `IWandable` interaction work server-side.

**Риски / зависимости:**

Some wand/focus content may overlap later content/client phases, but core wand storage/cost behavior is Stage 3.

### GAP-9: Inventory vis consumption is not reference-equivalent and Bauble scenarios need runtime validation

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/wands/WandManager.java:60-107`
- `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:153-188`
- `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:103-116`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/wands/ItemWandCasting.class`
- `thaumcraft_src/thaumcraft/common/items/baubles/ItemAmuletVis.class`

**Что не совпадает:**

Current inventory consumption returns after the first wand/amulet that can pay the entire cost (`src/main/java/thaumcraft/common/items/wands/WandManager.java:63-107`). It checks Vis Amulet before held/inventory wands and uses `crafting=true` for amulet but `crafting=false` for inventory wands (`src/main/java/thaumcraft/common/items/wands/WandManager.java:70-100`). Static analysis cannot confirm this matches original behavior for split costs, crafting discounts, Baubles slot order, multiple amulets, creative mode, and runic shield consumption. PRD explicitly calls out Bauble vis storage and inventory vis consumption runtime scenarios as known risk (`docs/PRD.md:249-251`).

**Что нужно доделать:**

Compare and validate reference `consumeAllVis`, `consumeAllVisCrafting`, `consumeVisFromInventory` semantics and port differences.

**Как доделать:**
- Decompile/reference-compare `WandManager` and `ItemWandCasting.consumeAllVis*` behavior.
- Decide whether costs can be split across multiple storage items; implement reference behavior.
- Ensure crafting vs non-crafting discount/centi-vis conversion is correct for wand and amulet.
- Add runtime/manual scenarios for mainhand wand, offhand wand, inventory wand, Vis Amulet, multiple baubles and insufficient partial vis.
- Files/classes: `WandManager`, `ItemWandCasting`, `ItemAmuletVis`, `InternalMethodHandler`.

**Критерии приемки:**
- [ ] Inventory vis consumption order and split/full-payment behavior matches reference.
- [ ] Baubles vis consumption works for runic shield recharge and crafting costs.
- [ ] Creative mode and `doit=false` checks do not mutate vis.
- [ ] Runtime scenarios from PRD known risk pass.

**Риски / зависимости:**

Requires runtime validation with Baubles loaded (`build.gradle:42-44`).

### GAP-10: Vis Amulet does not pull from TileVisRelay/network

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:123-137`
- `src/main/java/thaumcraft/api/visnet/VisNetHandler.java:59-84`
- `src/main/java/thaumcraft/api/visnet/TileVisNode.java:36-45`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/baubles/ItemAmuletVis.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileVisRelay.class`
- CFR reference `ItemAmuletVis.onWornTick` checks `TileVisRelay.nearbyPlayers`, consumes relay vis and triggers consume effect.

**Что не совпадает:**

Current `onWornTick` only transfers stored amulet vis into the held mainhand wand. Reference also charges the amulet from nearby `TileVisRelay` when the amulet has room. Current file contains no `TileVisRelay.nearbyPlayers` logic (`src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java:123-137`).

**Что нужно доделать:**

Port the relay-to-amulet charging branch and verify current `TileVisRelay` implementation exposes equivalent nearby-player tracking.

**Как доделать:**
- Inspect current `TileVisRelay` and reference `TileVisRelay.class`.
- Add nearby relay lookup and `consumeVis(aspect, amount)` pull for amulet room.
- Trigger relay consume effect after successful drain.
- Files/classes: `ItemAmuletVis`, `TileVisRelay`, `TileVisNode`, `VisNetHandler` if network traversal is wrong.

**Критерии приемки:**
- [ ] Amulet fills from nearby relay by up to reference amount every 5 ticks.
- [ ] Amulet still fills held wand from its storage.
- [ ] Relay consume effect triggers only when vis is drained.
- [ ] No server crash when Baubles handler or relay weak reference is absent.

**Риски / зависимости:**

Depends on current `TileVisRelay` parity, which may overlap aura/vis network runtime validation.

### GAP-11: Internal aspect generation and bonus tags are Stage 3 stubs

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/InternalMethodHandler.java:65-75`
- `src/main/java/thaumcraft/api/ThaumcraftApi.java:245-260`
- `src/main/java/thaumcraft/common/config/ConfigAspects.java:10-191`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.class` as referenced by CFR `ScanManager.completeScan`
- `thaumcraft_src/thaumcraft/common/lib/InternalMethodHandler.class`

**Что не совпадает:**

`getBonusObjectTags` returns an empty list with `// Phase 3: bonus tags from components` and `generateTags` returns an empty list with `// Phase 3: auto-generate tags from materials` (`src/main/java/thaumcraft/common/lib/InternalMethodHandler.java:65-75`). Reference scan completion uses object tags plus bonus tags before validating scans. Current `ThaumcraftApi.registerComplexObjectTag` calls `ThaumcraftApiHelper.generateTags` when a tag is missing (`src/main/java/thaumcraft/api/ThaumcraftApi.java:245-253`), so the stub causes missing/weak aspect tags for complex objects and addon API calls.

**Что нужно доделать:**

Port object aspect auto-generation and bonus tag logic from reference crafting/aspect manager behavior.

**Как доделать:**
- Decompile and port relevant methods from reference `ThaumcraftCraftingManager`/`InternalMethodHandler`.
- Implement material/component-derived tags for `generateTags(Item, int)`.
- Implement component/enchantment/container-derived bonus tags for `getBonusObjectTags(ItemStack, AspectList)`.
- Expand `ConfigAspects` registrations as needed for Thaumcraft and vanilla items.
- Files/classes: `InternalMethodHandler`, possibly new/common crafting helper, `ConfigAspects`, `ThaumcraftApiHelper` callers.

**Критерии приемки:**
- [ ] No Stage 3 placeholder comments remain in active aspect tag API paths.
- [ ] `registerComplexObjectTag` can generate non-empty tags for eligible unregistered items.
- [ ] Thaumometer scans use base plus bonus aspects.
- [ ] Addon-facing `ThaumcraftApiHelper.generateTags` and `getBonusObjectTags` return reference-compatible results.

**Риски / зависимости:**

Broad behavior surface; keep implementation minimal and reference-driven to avoid changing addon API signatures.

### GAP-12: Runic shielding uses hardcoded constants, incomplete sync/FX and simplified champion behavior

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:37-360`
- `src/main/java/thaumcraft/common/config/Config.java:63-67`
- `src/main/java/thaumcraft/common/config/Config.java:295-297`
- `src/main/java/thaumcraft/common/items/baubles/ItemRingRunic.java:48-57`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/events/EventHandlerRunic.class`
- `thaumcraft_src/thaumcraft/common/items/baubles/ItemRingRunic.class`
- `thaumcraft_src/thaumcraft/common/items/baubles/ItemAmuletRunic.class`
- `thaumcraft_src/thaumcraft/common/items/baubles/ItemGirdleRunic.class`

**Что не совпадает:**

Config exposes `shieldRecharge`, `shieldWait`, `shieldCost` (`src/main/java/thaumcraft/common/config/Config.java:63-67`, `src/main/java/thaumcraft/common/config/Config.java:295-297`), but `EventHandlerRunic` uses hardcoded `SHIELD_COST = 1`, `SHIELD_RECHARGE_MS = 60000L`, `SHIELD_WAIT_TICKS = 100` (`src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:48-52`). Packet/FX/sound sends are commented out at multiple charge and damage points (`src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:107-113`, `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:146-148`, `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:212-269`, `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:312-316`). Champion behavior is explicitly simplified because no champion attribute exists (`src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:273-294`). PRD calls Runic Ring tick behavior a known risk (`docs/PRD.md:249-253`).

**Что нужно доделать:**

Align runic shield charge timing/cost/upgrades/sync with reference and validate Runic Ring variants.

**Как доделать:**
- Replace hardcoded constants with `Config.shieldCost`, `Config.shieldRecharge`, `Config.shieldWait` after confirming units against reference.
- Restore packet sends for `PacketRunicCharge` and server-side FX packet sends where Stage 3 state requires them; client rendering can be validated in Phase 8 but packet state must be correct.
- Verify charged/healing/kinetic/emergency upgrade semantics for rings/amulets/girdles.
- Decide champion shield dependency separately; label as dependency if entity champion system is not Stage 3.
- Files/classes: `EventHandlerRunic`, `ItemRingRunic`, `ItemAmuletRunic`, `ItemGirdleRunic`, `PacketRunicCharge`, `Config`.

**Критерии приемки:**
- [ ] Runic recharge cost, delay and interval obey config and match reference units.
- [ ] Runic charge/max sync packet is sent on join, max change, recharge and damage.
- [ ] Runic Ring variants produce reference charge/upgrades over time.
- [ ] Damage absorption, kinetic explosion, healing and emergency recharge match reference behavior.

**Риски / зависимости:**

Champion mob details may depend on entity/champion systems outside Stage 3; keep that dependency minimal and do not block runic player shielding on champion FX.

### GAP-13: Aura/vis network implementation needs reference parity and runtime verification

**Статус:** требует проверки  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/api/visnet/VisNetHandler.java:20-229`
- `src/main/java/thaumcraft/api/visnet/TileVisNode.java:15-192`
- `src/main/java/thaumcraft/common/tiles/TileNode.java:31-208`

**Референс:**
- `thaumcraft_src/thaumcraft/api/visnet/VisNetHandler.class`
- `thaumcraft_src/thaumcraft/api/visnet/TileVisNode.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileNode.class`

**Что не совпадает:**

Current API has a similar structure to reference signatures, but static analysis found likely risky differences: `VisNetHandler.addNode` returns `null` when no source list exists and does not put the new empty map into `sources` (`src/main/java/thaumcraft/api/visnet/VisNetHandler.java:104-110`); `calculateNearbyNodes` also creates but does not store an empty map (`src/main/java/thaumcraft/api/visnet/VisNetHandler.java:167-172`). `canNodeBeSeen` compares `RayTraceResult.hitVec` floating-point coordinates to integer block coordinates (`src/main/java/thaumcraft/api/visnet/VisNetHandler.java:218-227`), which is suspicious for line-of-sight parity. `TileNode` has a custom regeneration/catch-up implementation that must be verified against reference NBT keys and tick rates (`src/main/java/thaumcraft/common/tiles/TileNode.java:31-61`, `src/main/java/thaumcraft/common/tiles/TileNode.java:143-207`).

**Что нужно доделать:**

Perform focused reference comparison and runtime tests for node registration, relay linking, drain order, line-of-sight and node recharge.

**Как доделать:**
- Decompile reference `VisNetHandler`, `TileVisNode`, `TileNode`, `TileVisRelay` and compare logic line-by-line.
- Fix source map/cache behavior if reference stores empty maps or handles absent dimensions differently.
- Verify NBT compatibility: `Aspects`, `AspectsBase`, `nodeId`, `lastActive`, `nodeVisBase`, `type`, `modifier`, `fuel`.
- Runtime scenarios: place source node, place relay, drain vis, unload/reload chunk, server restart, blocked line-of-sight.
- Files/classes: `VisNetHandler`, `TileVisNode`, `TileNode`, `TileVisRelay`, any node/relay blocks.

**Критерии приемки:**
- [ ] Nodes and relays register/reconnect after chunk reload.
- [ ] `drainVis` drains nearest valid visible node/relay path matching reference behavior.
- [ ] Node NBT loads old and new data without losing base aspects.
- [ ] Node recharge/catch-up matches reference or is explicitly documented.

**Риски / зависимости:**

Runtime world/chunk behavior cannot be proven statically.

### GAP-14: Thaumometer scan hook is server-only and lacks reference scan UX/state flow validation

**Статус:** частично реализовано  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java:35-58`
- `src/main/java/thaumcraft/common/lib/research/ScanManager.java:24-107`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketScannedToServer.java:18-98`

**Референс:**
- `thaumcraft_src/thaumcraft/common/items/relics/ItemThaumometer.class`
- `thaumcraft_src/thaumcraft/common/lib/research/ScanManager.class`

**Что не совпадает:**

Current Thaumometer immediately scans on right-click server-side, using `getPickBlock` fallback and no prefix selection (`src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java:35-55`). Reference scan flow distinguishes scan prefixes and client/server packet flow. Current `PacketScannedToServer` accepts a prefix field but `ItemThaumometer` does not use it (`src/main/java/thaumcraft/common/lib/network/playerdata/PacketScannedToServer.java:25-39`).

**Что нужно доделать:**

Align Thaumometer scan flow with reference once GAP-5 scan completion is implemented.

**Как доделать:**
- Decide server-only vs client-initiated flow for Forge 1.12.2 while preserving scan keys/prefixes.
- Ensure block, item, entity and node phenomena scans use the same `completeScan` backend.
- Add manual verification for repeated scans and invalid scans.
- Files/classes: `ItemThaumometer`, `PacketScannedToServer`, `ScanManager`, client scan UI if needed as dependency.

**Критерии приемки:**
- [ ] Thaumometer scan records reference-compatible keys.
- [ ] Client and server agree on scan success/failure.
- [ ] Repeated scans do not duplicate rewards.
- [ ] Node/phenomena targets can be scanned where reference allows.

**Риски / зависимости:**

Client presentation and scan animation are Phase 8 dependency; server state is Stage 3.

### GAP-15: Potions and enchantments compile but are not behavior-validated against reference

**Статус:** требует проверки  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/Config.java:201-228`
- `src/main/java/thaumcraft/common/Thaumcraft.java:263-287`
- `src/main/java/thaumcraft/common/lib/potions/PotionThaumarhia.java:31-46`
- `src/main/java/thaumcraft/common/lib/enchantment/EnchantmentFrugal.java:11-53`
- `src/main/java/thaumcraft/common/lib/enchantment/EnchantmentRepair.java`
- `src/main/java/thaumcraft/common/lib/enchantment/EnchantmentHaste.java`
- `src/main/java/thaumcraft/common/lib/enchantment/EnchantmentPotency.java`
- `src/main/java/thaumcraft/common/lib/enchantment/EnchantmentWandFortune.java`

**Референс:**
- `thaumcraft_src/thaumcraft/api/potions/PotionFluxTaint.class`
- `thaumcraft_src/thaumcraft/api/potions/PotionVisExhaust.class`
- `thaumcraft_src/thaumcraft/common/lib/potions/*.class`
- `thaumcraft_src/thaumcraft/common/lib/enchantment/*.class`

**Что не совпадает:**

Registrations exist, but behavior parity is not documented or runtime validated. Example: `EnchantmentFrugal.canApply` allows `ItemBook` and `Items.ENCHANTED_BOOK` and uses `ItemFocusBasic.acceptsEnchant` (`src/main/java/thaumcraft/common/lib/enchantment/EnchantmentFrugal.java:42-47`), matching the PRD baseline at a high level, but exact min/max enchantability, compatibility and focus upgrade interactions need reference verification. Potion implementations need verification for server tick effects, curatives, bad/good flags, status icons and registry names. PRD baseline says server potion effects exist but Stage 3 is not validated (`docs/PRD.md:244-247`).

**Что нужно доделать:**

Compare each potion/enchantment class with reference and run focused manual/runtime scenarios.

**Как доделать:**
- Decompile all reference potion/enchantment classes and compare methods.
- Verify registry names and localization/resource dependencies.
- Test Vis Exhaust discounts, Thaumarhia goo placement, Warp Ward behavior, Unnatural Hunger curatives, Death Gaze, Frugal/Potency/Wand Fortune/Haste/Repair applicability.
- Files/classes: `Config`, `Thaumcraft`, all `common/lib/potions/*`, all `common/lib/enchantment/*`, `WandManager`, `WarpEvents`.

**Критерии приемки:**
- [ ] All Stage 3 potions register and apply server-side behavior without crashes.
- [ ] Potion curatives/durations/amplifiers match reference where practical.
- [ ] All Stage 3 enchantments apply only to valid items and affect gameplay paths.
- [ ] Manual or automated scenarios cover each potion/enchantment.

**Риски / зависимости:**

Client icon rendering is Phase 8 dependency; server behavior remains Stage 3.

### GAP-16: Research registration/content absence blocks full research gameplay validation

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigResearch.java:3-8`
- `src/main/java/thaumcraft/common/Thaumcraft.java:227-231`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigResearch.class`
- `thaumcraft_src/thaumcraft/api/research/*.class`

**Что не совпадает:**

`ConfigResearch.init()` is a placeholder with `// Phase 9: register research entries` (`src/main/java/thaumcraft/common/config/ConfigResearch.java:5-7`), but Stage 3 research backend cannot be fully validated without at least enough research entries to exercise completion, requisites, warp grants, hidden/lost research and scan triggers. This is labeled as dependency because full content registration belongs to a later content phase, but it directly blocks Stage 3 acceptance scenarios around research sync timing and completion behavior.

**Что нужно доделать:**

Provide a minimal Stage 3 validation fixture or defer full content registration explicitly while still completing backend behavior.

**Как доделать:**
- Do not broaden Stage 3 into full content registration.
- Add or identify minimal research entries required to test backend if acceptable, or document exact manual test dependency on Phase 9.
- Ensure backend code does not crash with empty categories and works once entries are registered.
- Files/classes: `ConfigResearch`, `ResearchCategories`, `ResearchItem`, `ResearchManager`.

**Критерии приемки:**
- [ ] Stage 3 backend can complete and sync a registered research item.
- [ ] Empty/missing research categories do not crash Stage 3 systems.
- [ ] Later content registration can use Stage 3 backend without API changes.
- [ ] Any remaining content dependency is documented as dependency, not claimed as closure.

**Риски / зависимости:**

Dependency on later content/research registration work. Because this blocks full gameplay validation, Stage 3 cannot be called complete until either minimal validation entries exist or the dependency is formally accepted as a documented deferral.

## 6. Итоговый checklist закрытия Stage 3

### Утвержденные pragmatic closure decisions

Для закрытия Stage 3 принят вариант **Capability-primary fresh-world persistence**:

- Forge capability остается основным runtime/backend-хранилищем player knowledge.
- External 1.7.10 player data files are not Stage 3 scope; fresh worlds use capability state only.
- Save path должен писать authoritative capability state with documented NBT keys.
- `warpCounter` должен входить в authoritative Stage 3 sync, а не оставаться неявным server-only полем.
- Champion mob runic shielding не блокирует Stage 3: Stage 3 закрывает player runic shielding, а champion-specific behavior остается dependency на entity/champion systems.
- Полный `ConfigResearch` content registration остается Phase 9 scope. Для Stage 3 допустим minimal gated validation fixture или явно задокументированная Phase 9 dependency, но backend должен уметь complete/sync зарегистрированный research item без API changes.

Практичный порядок checkpoint'ов:

1. `knowledge+persistence`: aspect pools, capability save/load, username lookup, capability clone hardening.
2. `sync-authority`: full sync replace/wipe semantics, aspect pool/discovery packets, `warpCounter`, runic charge packet.
3. `scan-core`: reference-compatible scan keys, validation, rewards through aspect pools, shared Thaumometer/packet completion backend.
4. `object-tags`: replace active Stage 3 stubs in object tag generation and bonus tag paths.
5. `wands-baubles-runic`: rod/cap registration stacks, inventory/bauble vis consumption, Vis Amulet relay pull, player runic shielding config/sync.

- [ ] GAP-1 closed: capability-backed fresh-world player knowledge load/save works through relog/restart/death clone.
- [ ] GAP-2 closed: aspect pools and reference player knowledge helper semantics exist.
- [ ] GAP-3 closed: research notes, clue/requisite/completion backend behavior is ported.
- [ ] GAP-4 closed: offline username research/aspect lookup works.
- [ ] GAP-5 closed: scan keys, validation, rewards and node phenomena match reference.
- [ ] GAP-6 closed: sync packets are authoritative and mutation-complete.
- [ ] GAP-7 closed: capability lifecycle is validated for death, End return, relog and restart.
- [ ] GAP-8 closed: wand rods/caps/focus upgrade and object-in-use behavior match reference.
- [ ] GAP-9 closed: inventory/bauble vis consumption scenarios are reference-compatible and runtime-tested.
- [ ] GAP-10 closed: Vis Amulet charges from nearby relay/network as in reference.
- [ ] GAP-11 closed: Stage 3 aspect generation/bonus tag stubs are replaced.
- [ ] GAP-12 closed: runic shielding uses config, syncs charge and matches player shielding behavior.
- [ ] GAP-13 closed or explicitly validated: aura/vis network links, drains, reloads and recharges correctly.
- [ ] GAP-14 closed or explicitly separated: Thaumometer server scan flow records correct state.
- [ ] GAP-15 closed: potions/enchantments behavior is compared and manually/runtime validated.
- [ ] GAP-16 resolved as dependency or minimal validation fixture: research backend can be tested with registered entries.
- [ ] `./scripts/dev.sh compileJava` passes after implementation changes.
- [ ] `./scripts/dev.sh smoke-server` passes for Stage 3 server/common behavior.
- [ ] Manual Stage 3 scenarios are recorded: scan item/entity/node, complete research, relog/restart, consume wand/amulet vis, runic shield recharge/damage, warp event, potion/enchantment use.

## 7. Definition of Done

Stage 3 считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 3 реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 3;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 3 проверены вручную или тестами;
- ./docs/Stage3.md обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

- Решено: Stage 3 использует capability-primary persistence for fresh worlds. External 1.7.10 player data files are out of scope.
- Решено: full Phase 9 research content registration не переносится в Stage 3. Для validation допустим minimal gated fixture; если fixture не добавлен, зависимость на Phase 9 должна быть явно отмечена в closure report.
- Решено: `warpCounter` должен синхронизироваться как часть authoritative Stage 3 player-data sync.
- Решено: Stage 3 закрывает player runic shielding. Champion mob runic shielding остается dependency на entity/champion systems и не блокирует Stage 3 при документированном deferral.
